import contextlib
import io
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

import build_tables


class ApacheSiteMatchingTest(unittest.TestCase):
    def test_shared_file_and_rule_use_the_labelled_call_sites(self):
        source = """class Numbers {
    private final java.util.Random random = new java.util.Random();
    int displayNumber() {
        return random.nextInt();
    }
    byte saltByte() {
        return Integer.valueOf(random.nextInt()).byteValue();
    }
    int unrelatedNumber() {
        return random.nextInt(64);
    }
}
"""
        file = "example/Numbers.java"
        cases = [
            build_tables.Case("display", [file], "Random", "no misuse", {"C9"}, gt_line="60",
                              gt_code="random.nextInt()", gt_method="displayNumber"),
            build_tables.Case("salt", [file], "Random", "misuse", {"C9"}, gt_line="111",
                              gt_code="random.nextInt().byteValue()", gt_method="saltBuffer"),
        ]
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            project = root / "decompiled/example"
            project.mkdir(parents=True)
            (project / "Numbers.java").write_text(source, encoding="utf-8")

            def score(*lines):
                findings = [(file, project, {"srcFile": "Numbers.java", "ruleId": "C9", "lineNo": line}) for line in lines]
                with (patch.object(build_tables, "APACHE", root),
                      patch.object(build_tables, "load_findings", return_value=findings),
                      patch.object(build_tables, "USED", set()), contextlib.redirect_stdout(io.StringIO())):
                    alerts, verdicts = build_tables.build("apache", "A", cases)
                return {alert["line"]: (alert["benchmark_cases"], alert["benchmark_verdict"]) for alert in alerts}, {
                    row["case_id"]: row["verdict"] for row in verdicts
                }

            self.assertEqual(score(7), ({7: ("salt", "TP")}, {"display": "TN", "salt": "TP"}))
            self.assertEqual(score(4, 7, 10), (
                {4: ("display", "FP"), 7: ("salt", "TP"), 10: ("", "UNMATCHED SITE")},
                {"display": "FP", "salt": "TP"},
            ))


if __name__ == "__main__":
    unittest.main()
