pipeline {
  agent {
    node {
      label 'maven'
    }

  }
  stages {
    stage('拉取代码') {
      steps {
        container('maven') {
          git(url: 'https://gitee.com/deng_shui_bing/kai-yu-cloud.git', credentialsId: 'gitee', branch: 'release', changelog: true, poll: false)
        }

      }
    }

    stage('编译项目') {
      steps {
        container('maven') {
          sh 'ls'
          sh 'mvn clean package -Dmaven.test.skip=true'
          sh 'ls ruoyi-auth/'
        }

      }
    }

    stage('ha4yqs') {
      parallel {
        stage('打包kaiyu-auth镜像') {
          agent none
          steps {
            container('maven') {
              sh 'ls ruoyi-auth/target'
              sh 'docker build -t  ruoyi-auth-test:latest -f ruoyi-auth/Dockerfile ./ruoyi-auth/'
            }

          }
        }

        stage('打包kaiyu-gateway镜像') {
          agent none
          steps {
            container('maven') {
              sh 'ls ruoyi-gateway/target'
              sh 'docker build -t  ruoyi-gateway-test:latest -f ruoyi-gateway/Dockerfile ./ruoyi-gateway/'
            }

          }
        }

        stage('打包kaiyu-system镜像') {
          agent none
          steps {
            container('maven') {
              sh 'ls ruoyi-system/target'
              sh 'docker build -t  ruoyi-system-test:latest -f ruoyi-system/Dockerfile ./ruoyi-system/'
            }

          }
        }

        stage('打包kaiyu-content镜像') {
          agent none
          steps {
            container('maven') {
              sh 'ls kaiyu-content/target'
              sh 'docker build -t  kaiyu-content-test:latest -f kaiyu-content/Dockerfile ./kaiyu-content/'
            }

          }
        }

        stage('打包kaiyu-learning镜像') {
          agent none
          steps {
            container('maven') {
              sh 'ls kaiyu-learning/target'
              sh 'docker build -t  kaiyu-learning-test:latest -f kaiyu-learning/Dockerfile ./kaiyu-learning/'
            }

          }
        }

        stage('打包kaiyu-media镜像') {
          agent none
          steps {
            container('maven') {
              sh 'ls kaiyu-media/target'
              sh 'docker build -t  kaiyu-media-test:latest -f kaiyu-media/Dockerfile ./kaiyu-media/'
            }

          }
        }

        stage('打包ruoyi-file镜像') {
          agent none
          steps {
            container('maven') {
              sh 'ls ruoyi-file/target'
              sh 'docker build -t  ruoyi-file-test:latest -f ruoyi-file/Dockerfile ./ruoyi-file/'
            }

          }
        }

        stage('打包ruoyi-gen镜像') {
          agent none
          steps {
            container('maven') {
              sh 'ls ruoyi-gen/target'
              sh 'docker build -t  ruoyi-gen-test:latest -f ruoyi-gen/Dockerfile ./ruoyi-gen/'
            }

          }
        }

        stage('打包ruoyi-job镜像') {
          agent none
          steps {
            container('maven') {
              sh 'ls ruoyi-job/target'
              sh 'docker build -t  ruoyi-job-test:latest -f ruoyi-job/Dockerfile ./ruoyi-job/'
            }

          }
        }

        stage('打包ruoyi-visual镜像') {
          agent none
          steps {
            container('maven') {
              sh 'ls ruoyi-visual/target'
              sh 'docker build -t  ruoyi-visual-test:latest -f ruoyi-visual/Dockerfile ./ruoyi-visual/'
            }

          }
        }

      }
    }

    stage('ywgt0x') {
      parallel {
        stage('推送kaiyu-auth镜像') {
          agent none
          when {
            branch 'release'
          }
          steps {
            container('maven') {
              withCredentials([usernamePassword(credentialsId: 'aliyun-docker-registry', passwordVariable: 'DOCKER_PWD_VAL', usernameVariable: 'DOCKER_USER_VAL')]) {
                sh 'echo "$DOCKER_PWD_VAL" | docker login $REGISTRY -u "$DOCKER_USER_VAL" --password-stdin '
                sh 'docker tag  ruoyi-auth-test:latest $REGISTRY/$DOCKERHUB_NAMESPACE/ruoyi-auth-test:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push $REGISTRY/$DOCKERHUB_NAMESPACE/ruoyi-auth-test:SNAPSHOT-$BUILD_NUMBER'
              }

            }

          }
        }

        stage('推送kaiyu-gateway镜像') {
          agent none
          when {
            branch 'release'
          }
          steps {
            container('maven') {
              withCredentials([usernamePassword(credentialsId: 'aliyun-docker-registry', passwordVariable: 'DOCKER_PWD_VAL', usernameVariable: 'DOCKER_USER_VAL')]) {
                sh 'echo "$DOCKER_PWD_VAL" | docker login $REGISTRY -u "$DOCKER_USER_VAL" --password-stdin '
                sh 'docker tag  ruoyi-gateway-test:latest $REGISTRY/$DOCKERHUB_NAMESPACE/ruoyi-gateway-test:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push $REGISTRY/$DOCKERHUB_NAMESPACE/ruoyi-gateway-test:SNAPSHOT-$BUILD_NUMBER'
              }

            }

          }
        }

        stage('推送kaiyu-system镜像') {
          agent none
          when {
            branch 'release'
          }
          steps {
            container('maven') {
              withCredentials([usernamePassword(credentialsId: 'aliyun-docker-registry', passwordVariable: 'DOCKER_PWD_VAL', usernameVariable: 'DOCKER_USER_VAL')]) {
                sh 'echo "$DOCKER_PWD_VAL" | docker login $REGISTRY -u "$DOCKER_USER_VAL" --password-stdin '
                sh 'docker tag  ruoyi-system-test:latest $REGISTRY/$DOCKERHUB_NAMESPACE/ruoyi-system-test:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push $REGISTRY/$DOCKERHUB_NAMESPACE/ruoyi-system-test:SNAPSHOT-$BUILD_NUMBER'
              }

            }

          }
        }

        stage('推送kaiyu-content镜像') {
          agent none
          when {
            branch 'release'
          }
          steps {
            container('maven') {
              withCredentials([usernamePassword(credentialsId: 'aliyun-docker-registry', passwordVariable: 'DOCKER_PWD_VAL', usernameVariable: 'DOCKER_USER_VAL')]) {
                sh 'echo "$DOCKER_PWD_VAL" | docker login $REGISTRY -u "$DOCKER_USER_VAL" --password-stdin '
                sh 'docker tag  kaiyu-content-test:latest $REGISTRY/$DOCKERHUB_NAMESPACE/kaiyu-content-test:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push $REGISTRY/$DOCKERHUB_NAMESPACE/kaiyu-content-test:SNAPSHOT-$BUILD_NUMBER'
              }

            }

          }
        }

        stage('推送kaiyu-learning镜像') {
          agent none
          when {
            branch 'release'
          }
          steps {
            container('maven') {
              withCredentials([usernamePassword(credentialsId: 'aliyun-docker-registry', passwordVariable: 'DOCKER_PWD_VAL', usernameVariable: 'DOCKER_USER_VAL')]) {
                sh 'echo "$DOCKER_PWD_VAL" | docker login $REGISTRY -u "$DOCKER_USER_VAL" --password-stdin '
                sh 'docker tag  kaiyu-learning-test:latest $REGISTRY/$DOCKERHUB_NAMESPACE/kaiyu-learning-test:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push $REGISTRY/$DOCKERHUB_NAMESPACE/kaiyu-learning-test:SNAPSHOT-$BUILD_NUMBER'
              }

            }

          }
        }

        stage('推送kaiyu-media镜像') {
          agent none
          when {
            branch 'release'
          }
          steps {
            container('maven') {
              withCredentials([usernamePassword(credentialsId: 'aliyun-docker-registry', passwordVariable: 'DOCKER_PWD_VAL', usernameVariable: 'DOCKER_USER_VAL')]) {
                sh 'echo "$DOCKER_PWD_VAL" | docker login $REGISTRY -u "$DOCKER_USER_VAL" --password-stdin '
                sh 'docker tag  kaiyu-media-test:latest $REGISTRY/$DOCKERHUB_NAMESPACE/kaiyu-media-test:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push $REGISTRY/$DOCKERHUB_NAMESPACE/kaiyu-media-test:SNAPSHOT-$BUILD_NUMBER'
              }

            }

          }
        }

        stage('推送ruoyi-file镜像') {
          agent none
          when {
            branch 'release'
          }
          steps {
            container('maven') {
              withCredentials([usernamePassword(credentialsId: 'aliyun-docker-registry', passwordVariable: 'DOCKER_PWD_VAL', usernameVariable: 'DOCKER_USER_VAL')]) {
                sh 'echo "$DOCKER_PWD_VAL" | docker login $REGISTRY -u "$DOCKER_USER_VAL" --password-stdin '
                sh 'docker tag  ruoyi-file-test:latest $REGISTRY/$DOCKERHUB_NAMESPACE/ruoyi-file-test:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push $REGISTRY/$DOCKERHUB_NAMESPACE/ruoyi-file-test:SNAPSHOT-$BUILD_NUMBER'
              }

            }

          }
        }

        stage('推送ruoyi-gen镜像') {
          agent none
          when {
            branch 'release'
          }
          steps {
            container('maven') {
              withCredentials([usernamePassword(credentialsId: 'aliyun-docker-registry', passwordVariable: 'DOCKER_PWD_VAL', usernameVariable: 'DOCKER_USER_VAL')]) {
                sh 'echo "$DOCKER_PWD_VAL" | docker login $REGISTRY -u "$DOCKER_USER_VAL" --password-stdin '
                sh 'docker tag  ruoyi-gen-test:latest $REGISTRY/$DOCKERHUB_NAMESPACE/ruoyi-gen-test:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push $REGISTRY/$DOCKERHUB_NAMESPACE/ruoyi-gen-test:SNAPSHOT-$BUILD_NUMBER'
              }

            }

          }
        }

        stage('推送ruoyi-job镜像') {
          agent none
          when {
            branch 'release'
          }
          steps {
            container('maven') {
              withCredentials([usernamePassword(credentialsId: 'aliyun-docker-registry', passwordVariable: 'DOCKER_PWD_VAL', usernameVariable: 'DOCKER_USER_VAL')]) {
                sh 'echo "$DOCKER_PWD_VAL" | docker login $REGISTRY -u "$DOCKER_USER_VAL" --password-stdin '
                sh 'docker tag  ruoyi-job-test:latest $REGISTRY/$DOCKERHUB_NAMESPACE/ruoyi-job-test:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push $REGISTRY/$DOCKERHUB_NAMESPACE/ruoyi-job-test:SNAPSHOT-$BUILD_NUMBER'
              }

            }

          }
        }

        stage('推送ruoyi-visual镜像') {
          agent none
          when {
            branch 'release'
          }
          steps {
            container('maven') {
              withCredentials([usernamePassword(credentialsId: 'aliyun-docker-registry', passwordVariable: 'DOCKER_PWD_VAL', usernameVariable: 'DOCKER_USER_VAL')]) {
                sh 'echo "$DOCKER_PWD_VAL" | docker login $REGISTRY -u "$DOCKER_USER_VAL" --password-stdin '
                sh 'docker tag  ruoyi-visual-test:latest $REGISTRY/$DOCKERHUB_NAMESPACE/ruoyi-visual-test:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push $REGISTRY/$DOCKERHUB_NAMESPACE/ruoyi-visual-test:SNAPSHOT-$BUILD_NUMBER'
              }

            }

          }
        }

      }
    }

    stage('gekyzt') {
      parallel {
        stage('部署kaiyu-auth到dev环境') {
          agent none
          steps {
            container('maven') {
              input(message: 'deploy to dev? ', submitter: 'admin')
              withCredentials([kubeconfigContent(credentialsId: 'demo-kubeconfig', variable: 'KUBECONFIG_CONTENT')]) {
                sh 'mkdir ~/.kube'
                sh 'echo "$KUBECONFIG_CONTENT" > ~/.kube/config'
                sh 'envsubst < ruoyi-auth/deploy/deploy.yaml | kubectl apply -f -'
              }

            }

          }
        }

        stage('部署kaiyu-gateway到dev环境') {
          agent none
          steps {
            container('maven') {
              input(message: 'deploy to dev? ', submitter: 'admin')
              withCredentials([kubeconfigContent(credentialsId: 'demo-kubeconfig', variable: 'KUBECONFIG_CONTENT')]) {
                sh 'mkdir ~/.kube'
                sh 'echo "$KUBECONFIG_CONTENT" > ~/.kube/config'
                sh 'envsubst < ruoyi-gateway/deploy/deploy.yaml | kubectl apply -f -'
              }

            }

          }
        }

        stage('部署kaiyu-system到dev环境') {
          agent none
          steps {
            container('maven') {
              input(message: 'deploy to dev? ', submitter: 'admin')
              withCredentials([kubeconfigContent(credentialsId: 'demo-kubeconfig', variable: 'KUBECONFIG_CONTENT')]) {
                sh 'mkdir ~/.kube'
                sh 'echo "$KUBECONFIG_CONTENT" > ~/.kube/config'
                sh 'envsubst < ruoyi-system/deploy/deploy.yaml | kubectl apply -f -'
              }

            }

          }
        }

        stage('部署kaiyu-content到dev环境') {
          agent none
          steps {
            container('maven') {
              input(message: 'deploy to dev? ', submitter: 'admin')
              withCredentials([kubeconfigContent(credentialsId: 'demo-kubeconfig', variable: 'KUBECONFIG_CONTENT')]) {
                sh 'mkdir ~/.kube'
                sh 'echo "$KUBECONFIG_CONTENT" > ~/.kube/config'
                sh 'envsubst < kaiyu-content/deploy/deploy.yaml | kubectl apply -f -'
              }

            }

          }
        }

        stage('部署kaiyu-learning到dev环境') {
          agent none
          steps {
            container('maven') {
              input(message: 'deploy to dev? ', submitter: 'admin')
              withCredentials([kubeconfigContent(credentialsId: 'demo-kubeconfig', variable: 'KUBECONFIG_CONTENT')]) {
                sh 'mkdir ~/.kube'
                sh 'echo "$KUBECONFIG_CONTENT" > ~/.kube/config'
                sh 'envsubst < kaiyu-learning/deploy/deploy.yaml | kubectl apply -f -'
              }

            }

          }
        }

        stage('部署kaiyu-media到dev环境') {
          agent none
          steps {
            container('maven') {
              input(message: 'deploy to dev? ', submitter: 'admin')
              withCredentials([kubeconfigContent(credentialsId: 'demo-kubeconfig', variable: 'KUBECONFIG_CONTENT')]) {
                sh 'mkdir ~/.kube'
                sh 'echo "$KUBECONFIG_CONTENT" > ~/.kube/config'
                sh 'envsubst < kaiyu-media/deploy/deploy.yaml | kubectl apply -f -'
              }

            }

          }
        }

        stage('部署ruoyi-file到dev环境') {
          agent none
          steps {
            container('maven') {
              input(message: 'deploy to dev? ', submitter: 'admin')
              withCredentials([kubeconfigContent(credentialsId: 'demo-kubeconfig', variable: 'KUBECONFIG_CONTENT')]) {
                sh 'mkdir ~/.kube'
                sh 'echo "$KUBECONFIG_CONTENT" > ~/.kube/config'
                sh 'envsubst < ruoyi-file/deploy/deploy.yaml | kubectl apply -f -'
              }

            }

          }
        }

        stage('部署ruoyi-gen到dev环境') {
          agent none
          steps {
            container('maven') {
              input(message: 'deploy to dev? ', submitter: 'admin')
              withCredentials([kubeconfigContent(credentialsId: 'demo-kubeconfig', variable: 'KUBECONFIG_CONTENT')]) {
                sh 'mkdir ~/.kube'
                sh 'echo "$KUBECONFIG_CONTENT" > ~/.kube/config'
                sh 'envsubst < ruoyi-gen/deploy/deploy.yaml | kubectl apply -f -'
              }

            }

          }
        }

        stage('部署ruoyi-job到dev环境') {
          agent none
          steps {
            container('maven') {
              input(message: 'deploy to dev? ', submitter: 'admin')
              withCredentials([kubeconfigContent(credentialsId: 'demo-kubeconfig', variable: 'KUBECONFIG_CONTENT')]) {
                sh 'mkdir ~/.kube'
                sh 'echo "$KUBECONFIG_CONTENT" > ~/.kube/config'
                sh 'envsubst < ruoyi-job/deploy/deploy.yaml | kubectl apply -f -'
              }

            }

          }
        }

        stage('部署ruoyi-visual到dev环境') {
          agent none
          steps {
            container('maven') {
              input(message: 'deploy to dev? ', submitter: 'admin')
              withCredentials([kubeconfigContent(credentialsId: 'demo-kubeconfig', variable: 'KUBECONFIG_CONTENT')]) {
                sh 'mkdir ~/.kube'
                sh 'echo "$KUBECONFIG_CONTENT" > ~/.kube/config'
                sh 'envsubst < ruoyi-visual/deploy/deploy.yaml | kubectl apply -f -'
              }

            }

          }
        }

      }
    }

    stage('发送确认邮件') {
      agent none
      steps {
        mail(to: '1206085316@qq.com', cc: '', subject: '构建开宇智能在线教育平台Jenkins流水线测试结果', body: '测试构建 成功 SNAPSHOT-$BUILD_NUMBER')
      }
    }

    stage('部署到prod生产环境') {
      agent none
      steps {
        container('maven') {
          input(id: 'deploy-to-production', message: 'deploy to production?')
          withCredentials([kubeconfigContent(credentialsId : 'KUBECONFIG_CREDENTIAL_ID' ,variable : 'KUBECONFIG_CONFIG' ,)]) {
            sh 'mkdir -p ~/.kube/'
            sh 'echo "$KUBECONFIG_CONFIG" > ~/.kube/config'
            sh 'envsubst < deploy/prod-ol/deploy.yaml | kubectl apply -f -'
          }

        }

      }
    }

  }
  environment {
    DOCKER_CREDENTIAL_ID = 'dockerhub-id'
    GITHUB_CREDENTIAL_ID = 'github-id'
    KUBECONFIG_CREDENTIAL_ID = 'demo-kubeconfig'
    REGISTRY = 'registry.cn-hangzhou.aliyuncs.com'
    DOCKERHUB_NAMESPACE = 'ky_eudcation'
    GITHUB_ACCOUNT = 'kubesphere'
    APP_NAME = 'devops-java-sample'
  }
  parameters {
    string(name: 'TAG_NAME', defaultValue: '', description: '')
  }
}