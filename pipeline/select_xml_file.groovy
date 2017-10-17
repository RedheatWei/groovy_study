import org.boon.Boon;
//def manifest_files = "git@git.tclab.lenovo.com:thinkcloud_ci/manifests.git"
def manifest_files = "git@10.100.218.203:nfv/manifests.git"
def execute(cmd){
    def proc = cmd.execute()
    proc.waitFor()
    return proc
}
def getTmpDir(){
    def tmp_shell = execute("mktemp -d")
    def tmp_dir = tmp_shell.text.split("\n")[0]
    return tmp_dir
}
def cloneManifest(tmp_dir,manifest_files){

    def shell  = "/usr/bin/git clone ${manifest_files} ${tmp_dir}"
    def clone_git = execute(shell)
    println(clone_git.text)
}
def getXmlFiles(tmp_dir){
    def find_shell = "find ${tmp_dir} -name *.xml"
    println(find_shell)
    def outputStream = new StringBuffer();
    def proc = find_shell.execute()
    proc.waitForProcessOutput(outputStream, System.err);
    def file_str = outputStream.toString()
    return file_str.split("\n")
}
def changeToNeed(tmp_dir,file_list){
    file_name_list = []
    file_list.each{file_path ->
        file_name  = file_path.replace(tmp_dir+"/","")
//        file_name_list.push("FILE_"+file_name)
        file_name_list.push(file_name)
    }
    return file_name_list
}
def deleteTmpDir(tmp_dir){
    def dir_res = new File(tmp_dir)
    dir_res.deleteDir()
}
tmp_dir = getTmpDir()
cloneManifest(tmp_dir,manifest_files)
file_list = getXmlFiles(tmp_dir)
file_name_list = changeToNeed(tmp_dir,file_list).reverse()
deleteTmpDir(tmp_dir)
def jsonEditorOptions = Boon.fromJson(/{
        disable_edit_json: true,
        disable_properties: true,
        no_additional_properties: true,
        disable_collapse: true,
        disable_array_add: true,
        disable_array_delete: true,
        disable_array_reorder: true,
        theme: "bootstrap2",
        iconlib:"fontawesome4",
        schema: {
          title:""
          type: "string",
          enum: ${file_name_list}
          }
        }
}/);
return jsonEditorOptions
