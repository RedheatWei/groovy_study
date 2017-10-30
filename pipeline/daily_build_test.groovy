#!/usr/bin/env groovy
def MANIFESTS = "nfv_master.xml"
Date date = new Date()
def time = date.format("yyyy-MM-dd")
def manifests_name = 'manifests'  //程序集文件项目名称
def sync_script = '/data/python_project/git-sync/sync-no-change.py'
def group_name = 'nfv'  //项目组名称
def remmote_host = 'git.tclab.lenovo.com' //远程git ip
def local_host = '10.100.218.203' //本地git ip
def remote_git = "git@${remmote_host}:${group_name}/${manifests_name}.git" //远程git仓库
def local_git = "git@${local_host}:${group_name}/${manifests_name}.git" //本地git仓库
def UPDATE_GITLAB = "true"
def mode = "daily" //daily build
def manifest_file = MANIFESTS.split(/\./)[0]
def workspace_dir = "${BUILD_ID}/${manifest_file}/thinkcloud" //在Jenkins里创建的目录
def iso_dir = "/opt/ThinkCloud_iso/${group_name}/${JOB_NAME}/${manifest_file}/${mode}/${time}" //镜像存放目录
def url = "http://10.100.218.203:8099/${group_name}/${JOB_NAME}/${manifest_file}/${mode}/${time}"
def iso_name = "LenovoOpenStack-nfv_dev_4.1T_${mode}_${time}_${BUILD_ID}.iso"

def DEPLOY_MODE = "ha"
def DEPLOY_NET = "net1"


pipeline {
    agent any
    stages {
        stage("update gitlab"){
            steps{
                script{
                    if(UPDATE_GITLAB == "true"){
                        sh "python ${sync_script} ${remote_git} ${MANIFESTS}"
                    }
                }
            }
        }
        stage("download code"){
            steps{
                sh "[[ -d ${workspace_dir} ]] || mkdir -p ${workspace_dir};cd ${workspace_dir};repo init -u ${local_git} -m ${MANIFESTS};sed 's@${remmote_host}@${local_host}@g' -i .repo/${manifests_name}/${MANIFESTS};sed 's@10.240.205.131@${local_host}@g' -i .repo/${manifests_name}/${MANIFESTS};repo sync"
            }
        }
        stage('build iso'){
            steps{
                sh "chmod 777 -R ${workspace_dir};cd ${workspace_dir};./building/all_in_one.py -t ${mode} -n ${BUILD_ID}"
            }
        }
        stage('move iso'){
            steps{
                sh "[[ -d ${iso_dir} ]] || mkdir -p ${iso_dir};cd ${workspace_dir};mv LenovoOpenStack*.iso ${iso_dir};chmod -R a+rx /opt/ThinkCloud_iso"
            }
        }
        stage('scp iso'){
            steps {
                sh "scp ${iso_dir}/${iso_name} root@10.100.218.150:/tmp/daily && ssh root@10.100.218.150 'echo ${iso_name} ${DEPLOY_MODE} ${DEPLOY_NET} >> /tmp/daily/iso_name.log'"
                script{
                    try{
                        sh script: "ssh root@10.100.218.150 'cd /tmp/Auto_TC-2.0/bin/;sh run.sh /tmp/daily/${iso_name} /tmp/ThinkCloud_license_trial_ha_ceph_till20171231.zip net4 daily'"
                        def ha_status = 'SUCCESS'
                    }catch (e){
                        def ha_status = 'FAILURE'
                    }
                    try{
                        sh script: "ssh root@10.100.218.150 'cd /tmp/Auto_TC-2.0/bin/;sh run.sh /tmp/daily/${iso_name} /tmp/ThinkCloud_license_trial_multinode_ceph_till20171231.zip net5 daily'"
                        def multinode_status = 'SUCCESS'
                    }catch (e){
                        def multinode_status = 'FAILURE'
                    }
                }

            }
        }
    }
    post {
        always {
            emailext (
                    subject: "Jenkins Build Report",
                    body: """
Not HA auto deploy env:
Auto deploy success: ${multinode_status} 

HA auto deploy env: 
Auto deploy success: ${ha_status} 
                """,
                    to: "weipeng4@lenovo.com",
                    recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
        }
    }
}


