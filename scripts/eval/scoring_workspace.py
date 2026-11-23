"""Create a temporary directory for intermediate scoring files.

Successful runs remove the workspace. Failures retain it and print its path.
KEEP_LOGS=1 records Python commands and diagnostics in the workspace. Otherwise,
subprocess stdout is suppressed and stderr is inherited.
"""
from __future__ import annotations

import json
import os
import shutil
import subprocess
import sys
import tempfile
import traceback
from pathlib import Path


class ScoringWorkspace:
    def __init__(self, name: str):
        self.name = name
        self.keep_logs = os.environ.get("KEEP_LOGS") == "1"

    def __enter__(self):
        self.path = Path(tempfile.mkdtemp(prefix=f"cryptoeagle-{self.name}-"))
        return self

    def run_python(self, *args):
        command = [sys.executable, *map(str, args)]
        if not self.keep_logs:
            subprocess.run(command, check=True, stdout=subprocess.DEVNULL)
            return
        with (self.path / "commands.jsonl").open("a", encoding="utf-8") as record:
            record.write(json.dumps(command) + "\n")
        with (self.path / "diagnostics.log").open("a", encoding="utf-8") as log:
            subprocess.run(command, check=True, stdout=log, stderr=subprocess.STDOUT)

    def __exit__(self, kind, error, tb):
        if kind is None:
            shutil.rmtree(self.path)
        else:
            if self.keep_logs:
                with (self.path / "diagnostics.log").open("a", encoding="utf-8") as log:
                    traceback.print_exception(kind, error, tb, file=log)
            print(f"Scoring failed. Intermediate files retained at: {self.path}", file=sys.stderr)
        return False
