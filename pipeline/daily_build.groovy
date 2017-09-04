#!/usr/bin/env groovy
def MANIFESTS_LIST = ["nfv_master.xml","nfv_dev.xml"]
def mode = "daily" //手动构建
Date date = new Date()
def time = date.format("yyyy-MM-dd")
def workspace_dir = "thinkcloud/${BUILD_ID}" //在Jenkins里创建的目录
def remote_git = "git@10.240.205.131:nfv/manifests.git" //远程git仓库
def local_git = "git@10.100.218.203:nfv/manifests.git" //本地git仓库
def UPDATE_GITLAB = "true"

MANIFESTS_LIST.each{ MANIFESTS ->
    def manifest_file = MANIFESTS.split(/\./)[0]
    def iso_dir = "/opt/ThinkCloud_iso/nfv/${JOB_NAME}/${manifest_file}/${mode}/${time}" //镜像存放目录
    pipeline {
        agent any
        stages {
            stage("update gitlab"){
                steps{
                    script{
                        if(UPDATE_GITLAB == "true"){
                            sh "python /data/python_project/git-sync/sync-by-bash.py ${remote_git} ${MANIFESTS}"
                        }
                    }
                }
            }
            stage("download code"){
                steps{
                    sh "[[ -d ${workspace_dir} ]] || mkdir -p ${workspace_dir};cd ${workspace_dir};repo init -u ${local_git} -m ${MANIFESTS};repo sync"
                }
            }
            stage('build iso'){
                steps{
                    sh "./${workspace_dir}/building/all_in_one.py -t ${mode} -n ${BUILD_ID}"
                }
            }
            stage('move iso'){
                steps{
                    sh "[[ -d ${iso_dir} ]] || mkdir -p ${iso_dir};cd ${workspace_dir};mv LenovoOpenStack*.iso ${iso_dir};chmod -R a+rx /opt/ThinkCloud_iso"
                }
            }
        }
    }
}

