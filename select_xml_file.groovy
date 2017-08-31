//import org.boon.Boon;
def execute(cmd){
    def proc = cmd.execute()
    proc.waitFor()
}
def getTmpDir(){
    def tmp_shell = execute("mktemp -d")
    def tmp_dir = tmp_shell.text.split("\n")[0]
}
def cloneManifest(tmp_dir){
    def manifest_files = "git@10.240.205.131:nfv/manifests.git"
    def shell  = "/usr/bin/git clone ${manifest_files} ${tmp_dir}"
    def clone_git = execute(shell)
    println(clone_git.text)
}
def getXmlFiles(){
//    def find_shell = "find -name '*.xml'"
//    println(find_shell)
//    def file_list = find_shell.execute(null,new File(tmp_dir))
//    file_list.waitFor()
    def process=new ProcessBuilder("find -name '*.xml'").redirectErrorStream(true).start()
    process.inputStream.eachLine {println it}
}
tmp_dir = getTmpDir()
cloneManifest(tmp_dir)
getXmlFiles()

//def jsonEditorOptions = Boon.fromJson(/{
//        disable_edit_json: true,
//        disable_properties: true,
//        no_additional_properties: true,
//        disable_collapse: true,
//        disable_array_add: true,
//        disable_array_delete: true,
//        disable_array_reorder: true,
//        theme: "bootstrap2",
//        iconlib:"fontawesome4",
//        schema: {
//          type: "object",
//          properties: {
//            year: {
//              type: "string",
//              enum: [
//                ${git_clone.text}
//              ],
//              default: 2008
//            }
//          }
//        }
//}/);
