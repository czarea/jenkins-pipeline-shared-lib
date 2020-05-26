# jenkins pipeline

pipeline简单点，其实就是用代码控制之前job的所有流程，好处总结起来最大的两个亮点
- 代码化
- 流水线式处理，可扩张

## 格式

```groovy
pipeline {
    agent { node { label "build" } }
    options {
        timeout(time: 5, unit: 'MINUTES')
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {

        //阶段1 获取代码
        stage("CheckOut") {
            steps {
                script {
                    println("获取代码")
                }
            }
        }
        stage("Build") {
            steps {
                script {
                    println("运行构建")
                }
            }
        }
    }
    post {
        always {
            script {
                println("流水线结束后，经常做的事情")
            }
        }

        success {
            script {
                println("流水线成功后，要做的事情")
            }

        }
        failure {
            script {
                println("流水线失败后，要做的事情")
            }
        }

        aborted {
            script {
                println("流水线取消后，要做的事情")
            }

        }
    }
}
```

## pipeline shared lib

项目多了，可以使用Pipeline: Shared Groovy Libraries插件，提取公共的pipeline脚本，方便运维和管理。一般提取java，node，android构建的脚本。
提取之后java的构建可以使用少量代码就可以完成项目的发布。

```groovy
#!groovy
@Library('shared-libs')

def map = [:]
map.put('remote_host','xxx.xxx.xxx.xxx')
map.put('repo_url','git@xxx.git')
map.put('branch_name','xxx')
map.put('jar_name','xxx')
build(map)
```
