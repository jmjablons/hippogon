
def get_class_all(string_names) {
    string_names.collect {
        getPathClass(it)}}

def insert_into_hierarchy(name_children, name_parent) {
    annotats = getAnnotationObjects()
    anno_children = annotats.findAll{it.getPathClass() in get_class_all(name_children)}
    anno_parent = annotats.findAll{it.getPathClass() in get_class_all(name_parent)}
    for(parent in anno_parent) {
        anno_children.collect {
            getCurrentHierarchy().addPathObjectBelowParent(parent, it, true)}}}

insert_into_hierarchy(['ca1','ca2','ca3'], ['ca'])
insert_into_hierarchy(['dentate'], ['hippo'])
insert_into_hierarchy(['ca'], ['hippo'])
