#!groovy

/**
 *
 * @author zhouzx
 */
def call(Map map) {
    def remote = [:]
    remote.name = "${map.remote_host}"
    remote.host = "${map.remote_host}"
    remote.allowAnyHosts = true
    def tools = new com.czarea.tools()

    pipeline {
        agent any

        options {
            timestamps()
            disableConcurrentBuilds()
            buildDiscarder(logRotator(numToKeepStr: '20'))
            timeout(time: 5, unit: 'MINUTES')
        }
        environment {
            REPO_URL = "${map.repo_url}"
            BRANCH_NAME = "${map.branch_name}"
            MODEL = "${map.model}"
            JAR_NAME = "${map.jar_name}"
        }

        stages {
            stage('get code') {
                steps {
                    git branch: "${BRANCH_NAME}", credentialsId: 'gitlab', url: "${REPO_URL}"
                }
            }

            stage('build code') {
                steps {
                    script {
                        tools.logger("${map.model}",'blue')
                        if ("null" != "${MODEL}") {
                            tools.logger("clean build model ${map.jar_name}", 'blue')
                            sh 'chmod +x gradlew'
                            sh "./gradlew :${MODEL}:clean build -x test"
                        } else {
                            tools.logger('clean build', 'blue')
                            sh 'chmod +x gradlew'
                            sh './gradlew clean build -x test'
                        }
                    }
                }
            }

            stage('restart app') {
                steps {
                    script {
                        withCredentials([usernamePassword(credentialsId: 'btsp', passwordVariable: 'password', usernameVariable: 'username')]) {
                            remote.user = username
                            remote.password = password
                            script {
                                if ("null" != "${MODEL}") {
                                    sshPut remote: remote, from: "./${MODEL}/build/libs/${JAR_NAME}.jar", into: '/home/btsp/kll/'
                                    sshCommand remote: remote, command: "cd /home/btsp/kll/${map.jar_name} && ./start.sh"
                                } else {
                                    sshPut remote: remote, from: "./build/libs/${map.jar_name}.jar", into: '/home/btsp/kll/'
                                    sshCommand remote: remote, command: "cd /home/btsp/kll/${map.jar_name} && ./start.sh"
                                }
                            }
                        }
                    }
                }
            }
        }


        post {
            always {
                script {
                    tools.logger("always do ................", "green1")
                }
            }

            success {
                script {
                    tools.logger("build ................. success !", "green")
                }

            }
            failure {
                script {
                    tools.logger("build .................. failure !", "red")
                }
            }

            aborted {
                script {
                    tools.logger("build ................... aborted !", "red")
                }
            }
        }
    }

}
