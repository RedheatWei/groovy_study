import org.boon.Boon;
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
def cloneManifest(tmp_dir){
    def manifest_files = "git@10.240.205.131:nfv/manifests.git"
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
        file_name_list.push(file_name)
    }
    return file_name_list
}
tmp_dir = getTmpDir()
cloneManifest(tmp_dir)
file_list = getXmlFiles(tmp_dir)
file_name_list = changeToNeed(tmp_dir,file_list)

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
          type: "object",
          properties: {
            year: {
              type: "string",
              enum: ${file_name_list}
              default: 2008
            }
          }
        }
}/);
return jsonEditorOptions
