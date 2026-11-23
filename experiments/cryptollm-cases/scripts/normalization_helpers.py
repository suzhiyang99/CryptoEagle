"""API type preservation used by the case-level CA-CPG slicer."""


def protected_extractor(original, protected):
    """Keep CPG-resolved API type names out of the authors' variable renaming list."""
    names = set(protected) | {n.rsplit(".", 1)[-1] for n in protected}
    def extract(path):
        return [w for w in original(path) if w.replace('"', '').replace("\\", "") not in names]
    return extract
