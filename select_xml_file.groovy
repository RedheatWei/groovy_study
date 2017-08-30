//import org.boon.Boon;
def manifest_files = "git@10.240.205.131:nfv/manifests.git"
def shell = "cd `mkdir -d`;git clone ${manifest_files}"
println(shell)
Process git_clone = shell.execute()
println(git_clone.text)
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
