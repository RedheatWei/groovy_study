//import org.boon.Boon;
def execute(cmd){
    def proc = cmd.execute()
    proc.waitFor()
}
def manifest_files = "git@10.240.205.131:nfv/manifests.git"
def tmp_shell = "mktemp -d".execute()
def tmp_dir = tmp_shell.text.split("\n")[0]
def shell  = "/usr/bin/git clone ${manifest_files} ${tmp_dir}"
try{
    def clone_git = shell.execute()
    println(clone_git.text)
} catch(Exception e){
    println e1
}
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
