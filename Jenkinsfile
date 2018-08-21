pipeline{
    agent any
     stages {
        stage('Build') {
         steps {
             script{
             if(***REMOVED***.BRANCH_NAME=='testci'){
                 slackSend (message: "BUILD START: Job '${***REMOVED***.JOB_NAME} [${***REMOVED***.BUILD_NUMBER}]' CHECK THE RESULT ON: ***REMOVED***://***REMOVED******REMOVED***.***REMOVED***.it/blue/organizations/jenkins/CI-Catalog_Manager/activity")
                sh '''
                sbt " -DSTAGING=true; reload; clean; compile;  docker:publish"               
'''
                }
            }
         }
        }
        stage('Staging'){
            steps{
            script{
                if(***REMOVED***.BRANCH_NAME=='testci'){
                    sh '''
                    cd kubernetes
                    sh config-map-test.sh
                    kubectl apply -f  ***REMOVED***_catalog_manager_test.yml 
                    '''
                    slackSend (color: '#00FF00', message: "SUCCESSFUL: Job '${***REMOVED***.JOB_NAME} [${***REMOVED***.BUILD_NUMBER}]' ***REMOVED***://***REMOVED******REMOVED***.***REMOVED***.it/blue/organizations/jenkins/CI-Catalog_Manager/activity")
            }
            }
        }
     }
     }
     post { 
        failure { 
            slackSend (color: '#ff0000', message: "FAIL: Job '${***REMOVED***.JOB_NAME} [${***REMOVED***.BUILD_NUMBER}]' ***REMOVED***://***REMOVED******REMOVED***.***REMOVED***.it/blue/organizations/jenkins/CI-Catalog_Manager/activity")
        }
    }
}