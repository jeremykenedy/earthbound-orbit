#!/usr/bin/env python3
"""Check that maintained Markdown links resolve inside the repository."""

import re
from pathlib import Path


ROOT = Path(__file__).resolve().parent.parent
LINK = re.compile(r"\[[^\]]+\]\(([^)]+)\)")
REQUIRED_README_SECTIONS = {
    "Privacy", "Features", "Requirements", "Installation", "Fire TV Toolkit",
    "Configuration", "Screenshots", "Device verification", "Building and testing",
    "Documentation", "Release notes", "License",
}


def heading_text(markdown):
    return {line.lstrip("# ").strip() for line in markdown.splitlines() if line.startswith("#")}


def local_targets(path, markdown):
    for target in LINK.findall(markdown):
        target = target.split("#", 1)[0]
        if target and not target.startswith(("https://", "http://", "mailto:")):
            yield path.parent / target


def check_repository(root=ROOT):
    errors = []
    readme = root / "README.md"
    readme_text = readme.read_text(encoding="utf-8")
    missing_sections = REQUIRED_README_SECTIONS - heading_text(readme_text)
    if missing_sections:
        errors.append("README is missing sections: {}".format(", ".join(sorted(missing_sections))))
    markdown_files = [readme] + sorted((root / "docs").glob("*.md"))
    for path in markdown_files:
        content = path.read_text(encoding="utf-8")
        for target in local_targets(path, content):
            if not target.exists():
                errors.append("{} links to missing file {}".format(path.relative_to(root), target.relative_to(root)))
    for path in sorted((root / "docs").glob("*.md")):
        relative = path.relative_to(root).as_posix()
        if "({})".format(relative) not in readme_text:
            errors.append("README does not link to {}".format(relative))
    return errors


if __name__ == "__main__":
    problems = check_repository()
    if problems:
        for problem in problems:
            print(problem)
        raise SystemExit(1)
    print("Documentation links and required README sections are valid.")
