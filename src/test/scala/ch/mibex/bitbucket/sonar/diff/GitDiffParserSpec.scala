    scala.io.Source.fromInputStream(getClass.getResourceAsStream(path)).mkString.replaceAll("\u0085", "")
    }

    "parse diff with another u2028 new line character" in {
      allDiffs must succeedOn(readFile("/diffs/u2028-char-issue.txt"))
    }
    "parse spaces in the git diff file path" in {
      allDiffs must succeedOn(readFile("/diffs/spaces-in-git-diff-path.txt"))
    }
