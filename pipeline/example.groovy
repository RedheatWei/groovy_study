
node {
    stage('update gitlab'){
        sh "python /data/python_project/git-sync/sync-by-bash.py git@10.240.205.131:nfv/manifests.git ${XML_FILE}.xml"
    }
    stage('download code'){
        sh 'dir=thinkcloud/${BUILD_ID};[[ -d $dir ]] || mkdir -p $dir;cd $dir'
        sh 'repo init -u git@10.100.218.203:nfv/manifests.git -m ${XML_FILE}.xml'
        sh 'repo sync'
    }
    stage('build iso'){
        sh "./building/all_in_one.py -t manual -n ${BUILD_ID}"
    }
    stage('move iso'){
        sh 'iso_dir=/opt/ThinkCloud_iso/nfv/${XML_FILE}/manual/$(date +%Y-%m-%d);[[ -d $iso_dir ]] || mkdir -p $iso_dir;mv LenovoOpenStack*.iso $iso_dir;chmod -R a+rx $iso_dir'
    }

}
