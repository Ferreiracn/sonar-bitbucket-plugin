import ch.mibex.bitbucket.sonar.utils.StringUtils
import com.ctc.wstx.util.StringUtil
  val bitbucketClient = mock[BitbucketClient]
    "only yield issues on changed lines in complex diff" in new ComplexIssueContext {
      bitbucketClient.getPullRequestDiff(pullRequest) returns StringUtils.readFile("/diffs/5diffs-example.diff")
    "yield no issues when none in diff" in new NoNewIssuesContext {
      val pullRequest = PullRequest(id = 2, srcBranch = "develop", srcCommitHash = "", dstCommitHash = "")
      val inputFileCache = mock[InputFileCache]
      (inputFileCache.resolveRepoRelativePath("com.company:sonar-bitbucket-test:src/main/java/com/company/sonar/bitbucket/SimpleClass.java")
        returns Option("src/main/java/com/company/sonar/bitbucket/SimpleClass.java"))
      bitbucketClient.getPullRequestDiff(pullRequest) returns StringUtils.readFile("/diffs/2diffs-example.diff")

      val issuesOnChangedLinesFilter = new IssuesOnChangedLinesFilter(bitbucketClient, inputFileCache)
      val onlyIssuesOnChangedLines = issuesOnChangedLinesFilter.filter(pullRequest, issues.toList)

      onlyIssuesOnChangedLines must beEmpty
    }

  class NoNewIssuesContext extends Scope {
    val issue1 = mock[Issue]
    issue1.severity() returns Severity.MINOR
    issue1.message() returns "Replace all tab characters in this file by sequences of white-spaces."
    issue1.line() returns null
    issue1.ruleKey() returns RuleKey.parse("squid:S00105")
    issue1.componentKey() returns "com.company:sonar-bitbucket-test:src/main/java/com/company/sonar/bitbucket/SimpleClass.java"

    val issue2 = mock[Issue]
    issue2.severity() returns Severity.MAJOR
    issue2.message() returns """Move the "" string literal on the left side of this string comparison."""
    issue2.line() returns 5
    issue2.ruleKey() returns RuleKey.parse("squid:S1132")
    issue2.componentKey() returns "com.company:sonar-bitbucket-test:src/main/java/com/company/sonar/bitbucket/SimpleClass.java"

    val issue3 = mock[Issue]
    issue3.severity() returns Severity.MAJOR
    issue3.message() returns """Introduce a new variable instead of reusing the parameter "bar"."""
    issue3.line() returns 6
    issue3.ruleKey() returns RuleKey.parse("squid:S1226")
    issue3.componentKey() returns "com.company:sonar-bitbucket-test:src/main/java/com/company/sonar/bitbucket/SimpleClass.java"

    val issues = Set(issue1, issue2, issue3)
  }
  class ComplexIssueContext extends Scope {