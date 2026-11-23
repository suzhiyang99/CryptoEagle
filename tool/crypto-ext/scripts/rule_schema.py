"""Explicit migration from the archived C1-C14/X1-X13 schema and from earlier C1-C16 reports to the C1-C18 rule set."""
RULE_SET = "C1-C18"
CORE_IDS = tuple(f"C{i}" for i in range(1, 19))
# Earlier report labels: "core16-v1" (C1-C16) and "core18-v1" (the C1-C18 set before it was renamed). Their ids keep
# their meaning in C1-C18; C2/C11/C15 only gained additional sub-cases.
PREVIOUS_SETS = {"core16-v1": tuple(f"C{i}" for i in range(1, 17)), "core18-v1": tuple(f"C{i}" for i in range(1, 19))}
LEGACY_TO_CORE = {**{f"C{i}": f"C{i}" for i in range(1, 14)},
                  "C14": "C4", "X1": "C18", "X2": "C18", "X3": "C17", "X4": "C17",
                  "X6": "C2", "X7": "C14", "X8": "C9", "X9": "C13", "X10": "C15", "X11": "C16"}
RETIRED = frozenset({"X5", "X12", "X13"})

def migrate_findings(findings, schema="auto"):
    """Unversioned native reports are historical. New native JSON carries ruleSet.

    Callers supplying current hand-built findings may use schema='C1-C18'.
    Do not infer a schema from C14: its meaning changed.
    """
    migrated=[]
    for finding in findings:
        source = finding.get("ruleSet") if schema == "auto" else schema
        if source == RULE_SET or source in PREVIOUS_SETS:
            rule=finding.get("ruleId")
            if rule not in (CORE_IDS if source == RULE_SET else PREVIOUS_SETS[source]):
                raise ValueError(f"Invalid {source} rule: {rule}")
        elif source in (None, "legacy27"):
            old=finding.get("ruleId", "")
            if old in RETIRED: continue
            if old not in LEGACY_TO_CORE: raise ValueError(f"Unknown legacy rule: {old}")
            rule=LEGACY_TO_CORE[old]
        else: raise ValueError(f"Unknown rule schema: {source}")
        migrated.append(dict(finding, ruleId=rule, ruleSet=RULE_SET))
    return migrated
