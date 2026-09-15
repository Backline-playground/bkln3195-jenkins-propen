// BKLN-3195 E2E fixture. The `ci-mode` file on the branch selects the outcome:
// pass | test-fail | deploy-fail | unstable | sleep
pipeline {
  agent { label 'jenkins-jenkins-agent' }
  stages {
    stage('Build') {
      steps { sh 'echo compiling payments-service; echo BUILD OK' }
    }
    stage('Test') {
      steps {
        script {
          def mode = fileExists('ci-mode') ? readFile('ci-mode').trim() : 'pass'
          echo "ci-mode=${mode}"
          if (mode == 'test-fail') {
            for (int i = 1; i <= 40; i++) { echo "PaymentServiceTest.case${i} passed" }
            echo '[ERROR] PaymentServiceTest.refundsNegativeAmount FAILED: expected status 400 but was 500'
            echo '[ERROR]   at com.backline.bkln3195.PaymentServiceTest.refundsNegativeAmount(PaymentServiceTest.java:88)'
            error('Tests failed: 1 failure, 40 passed')
          }
          if (mode == 'unstable') {
            writeFile file: 'target/surefire-reports/TEST-PaymentServiceTest.xml', text: '''<?xml version="1.0" encoding="UTF-8"?>
<testsuite name="com.backline.bkln3195.PaymentServiceTest" tests="3" failures="1" errors="0" skipped="0" time="0.42">
  <testcase name="refundsPositiveAmount" classname="com.backline.bkln3195.PaymentServiceTest" time="0.1"/>
  <testcase name="refundsZeroAmount" classname="com.backline.bkln3195.PaymentServiceTest" time="0.1"/>
  <testcase name="refundsNegativeAmount" classname="com.backline.bkln3195.PaymentServiceTest" time="0.2">
    <failure message="expected status 400 but was 500" type="java.lang.AssertionError">java.lang.AssertionError: expected status 400 but was 500
	at com.backline.bkln3195.PaymentServiceTest.refundsNegativeAmount(PaymentServiceTest.java:88)</failure>
  </testcase>
</testsuite>'''
            junit 'target/surefire-reports/*.xml'
          }
          if (mode == 'sleep') { sleep time: 150, unit: 'MINUTES' }
          echo 'all tests passed'
        }
      }
    }
    stage('Deploy') {
      steps {
        script {
          def mode = fileExists('ci-mode') ? readFile('ci-mode').trim() : 'pass'
          if (mode == 'deploy-fail') {
            echo 'Packaging helm chart payments-service-1.4.2.tgz'
            echo 'Uploading to s3://acme-helm-charts/payments-service/'
            echo 'upload failed: An error occurred (AccessDenied) when calling the PutObject operation: User: arn:aws:sts::123456789012:assumed-role/jenkins-agent is not authorized to perform: s3:PutObject'
            error('Deploy failed: chart upload rejected by S3')
          }
          echo 'deploying payments-service to staging'
        }
      }
    }
  }
}
