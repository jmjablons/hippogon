/* import */
//import org.locationtech.jts.geom.Geometry
//import qupath.lib.common.GeneralTools
import qupath.lib.objects.*
import qupath.lib.roi.*
import static qupath.lib.gui.scripting.QPEx.*

/* values */
name_class = [
hippo = 'hippo',
dentate = 'dentate',
ca = 'ca',
ca1 = 'ca1',
ca2 = 'ca2',
ca3 = 'ca3',
'temp_ca']

names = [ca1 = 'ca1', ca3 = 'ca3', ca2 = 'ca2']

/* DIVIDE HIPPOCUMAPUS FORMATION
*
* TURORIAL:
* 1. MARK WHOLE HIPPOCAMPUS, SET CLASS HIPPO
* 2. MARK DENTATE GYRUS (LEAVE EDGES), SET CLASS DENTATE
* 3. MARK CA2 (LEAVE EDGES), SET CLASS CA2
* 4. USE SCRIPT
*/

/* init */
annos = getAnnotationObjects()

/* step 1 divide HIPPO into CA and DG */
anno_whole = annos.findAll{it.getPathClass() in get_class_all([name_class[0]])}
anno_sub = annos.findAll{it.getPathClass() in get_class_all([name_class[1]])}
for (anno in anno_whole){
  for (sub in anno_sub){
    def sub_roi = sub.getROI()
    def sub_geom = sub_roi.getGeometry()
    def anno_roi = anno.getROI()
    def anno_geom = anno_roi.getGeometry()
    /* fix sub */
    inter = anno_geom.intersection(sub_geom)
    inter_roi = GeometryTools.geometryToROI(inter, anno_roi.getImagePlane())
    inter_anno = PathObjects.createAnnotationObject(inter_roi, getPathClass(name_class[1]))
    addObject(inter_anno)
    removeObjects(anno_sub, true)
    /* update */
    sub = inter_anno
    sub_roi = sub.getROI()
    sub_geom = sub_roi.getGeometry()
    /* create ca */
    temp_intersect = anno_geom.difference(sub_geom)
    //inter_roi = GeometryTools.geometryToROI(temp_intersect, anno_roi.getImagePlane())
    inter_out = PathObjects.createAnnotationObject(inter_roi, getPathClass(name_class[2]))
    anno = inter_out
  }
  addObject(anno)
}

/* step 2 fix ca2 */
annos = getAnnotationObjects()
anno_whole = annos.findAll{it.getPathClass() in getPathClass(name_class[2])} //ca
anno_sub = annos.findAll{it.getPathClass() in getPathClass(name_class[4])} //ca2
for (anno in anno_whole){
  for (sub in anno_sub){
    def sub_roi = sub.getROI()
    def sub_roi_geom = sub_roi.getGeometry()
    def anno_roi = anno.getROI()
    def anno_geom = anno_roi.getGeometry()
    /* fix ca2 */
    inter = anno_geom.intersection(sub_roi_geom)
    inter_roi = GeometryTools.geometryToROI(inter, anno_roi.getImagePlane())
    inter_anno = PathObjects.createAnnotationObject(inter_roi, getPathClass(name_class[4]))
    addObject(inter_anno)
    removeObjects(anno_sub, true)
  }
}

/* add split part ca into (ca3+ca1) by ca2 */
annos = getAnnotationObjects()
anno_splitby = annos.findAll{it.getPathClass() in getPathClass(name_class[4])} //ca2
for (anno in anno_whole){
  for (sub in anno_splitby){
    sub_roi = sub.getROI()
    anno_roi = anno.getROI()
    inter_roi = RoiTools.difference(anno_roi, sub_roi)
    inter_anno = PathObjects.createAnnotationObject(inter_roi, getPathClass(name_class[6]))
    addObject(inter_anno)
    //removeObjects(anno_splitby, true)
  }
}

/* add split ca3+ca1 into ca3 and ca1 */
annos = getAnnotationObjects()
anno_whole = annos.findAll{it.getPathClass() in getPathClass(name_class[6])}
anno_splitby = annos.findAll{it.getPathClass() in getPathClass(name_class[4])} //ca2

anno_whole.each{
  all_polygons = []
  polygons = RoiTools.splitROI(it.getROI())
  i_name = 0
  for(ploy in polygons) {
    if(ploy) {
      all_polygons[i_name] = new PathAnnotationObject(ploy, getPathClass(names[i_name]))
      i_name = i_name + 1
    }
  }
  addObjects(all_polygons)
  print(all_polygons)
  removeObject(it, true)
}

println("+")
