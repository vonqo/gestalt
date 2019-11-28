####### hqz exporter for maya V0.6.1 ##############
#   Damien Picard 2014
#
#	HQZ by M. Elizabeth Scott - http://scanlime.org/
#
# Usage: 
# - run script
# - select objects to setup
# - click "Prepare scene" and switch viewport to front view
# - change settings according to your needs
# - write materials to the materials list
# - if you need more materials, add sliders and corresponding stuff to the interface...
# - in the object channel box, select the material you wish to assign (default = 0)
# - add spot or point light objects
# - change light color and intensity : set color value to 0 for white light or choose hue
#   alternatively you can also activate hqzSpectralLight and choose a spectral range between 400 & 700 nm (Start/End)
# - rotate spots only around the Z axis
# - if you wish to export normals (specially for caustics), I recommand activating check_Z, getting your
#   object vertices on the XY plane and extruding all edges to Z. this makes sure that only edges
#   on the XY plane are exported, and the normals are more predictable and controlable.
# - select objects to export again
# - click "Export"
# - that's pretty much it
#
# spectral color cheatsheet
#
#     400        450        500        550        600        650        700   nm -->
#    PURPLE      BLUE    CYAN    GREEN     YELLOW     ORANGE   RED


###################################

#  TODO import json (?!)
#       [%i] % etc.

import maya.cmds as cmds
from math import pi, floor, fabs, atan2
import os.path


#####SETTINGS

debug = False #remove all newlines to read with wireframe.html

#####UTILITY FUNCTIONS

def rgb2hsv(color):
    r, g, b = color[0]/255.0, color[1]/255.0, color[2]/255.0
    mx = max(r, g, b)
    mn = min(r, g, b)
    df = mx-mn
    if mx == mn:
        h = 0
    elif mx == r:
        h = (60 * ((g-b)/df) + 360) % 360
    elif mx == g:
        h = (60 * ((b-r)/df) + 120) % 360
    elif mx == b:
        h = (60 * ((r-g)/df) + 240) % 360
    if mx == 0:
        s = 0
    else:
        s = df/mx
    v = mx
    return h/360.0, s, v

def HSV2wavelength(color):
    
    h,s,v = rgb2hsv(color)
    wavelength = ((0.7-h)-floor((0.7-h)))*300+400
    if v == 0:
        wavelength = 0
    return wavelength
    
def get_loc(object):
    loc = cmds.xform(object, worldSpace = True, translation=True, query=True)
    x, y = loc[0]*resolution_x, loc[1]*resolution_x
    return x,y

def get_rot(object):
#    rot = cmds.xform(object, absolute=True, rotation=True, query=True)
    selection = cmds.ls(sl=True)
    loc = cmds.spaceLocator(position=(0,0,0))
    orient_cons = cmds.orientConstraint(object,loc)
    cmds.delete(orient_cons)
    cmds.move(0,0,-1, loc, relative = True, objectSpace = True)
    vec = []
    vec.append(cmds.xform(loc, absolute=True, translation=True, query=True)[0])
    vec.append(cmds.xform(loc, absolute=True, translation=True, query=True)[1])
    rot = vector2rotation(vec)
    cmds.delete(loc)
    try:
        cmds.select(selection, replace=True)
    except:
        pass
    return -rot


def vector2rotation(vector2d):
    null_vec = [1,0]
    vec_angle = (atan2(vector2d[1],vector2d[0]) - atan2(null_vec[1],null_vec[0]))*180/pi
    if invert_normals:
        vec_angle = 180 - vec_angle
    else:
        vec_angle *= -1
    if vec_angle < -180:
        vec_angle += 360
    if vec_angle > 180:
        vec_angle -= 360
    
    return vec_angle



def get_ui_values():
    global engine_path, folder, file_name, export_batch, resolution_x, resolution_y, exposure, gamma, \
        ray_number, time, seed, export_animation, start_frame, end_frame, export_normals, invert_normals, check_Z, materials
        
    engine_path = cmds.textFieldButtonGrp(engine_field, query=True, text=True)
    folder = cmds.textFieldButtonGrp(directory_field, query=True, text=True)
    file_name = cmds.textFieldGrp(file_field, query=True, text=True)
    
    export_batch = cmds.checkBoxGrp(batch_field, query=True, value1=True)
    
    resolution_x = cmds.intFieldGrp(resolution_field, query=True, value1=True)
    resolution_y = cmds.intFieldGrp(resolution_field, query=True, value2=True)
    exposure = cmds.floatSliderGrp(exposure_field, query=True, value=True)
    gamma = cmds.floatSliderGrp(gamma_field, query=True, value = True)
    ray_number = cmds.intSliderGrp(rays_field, query=True, value=True)
    seed = cmds.intSliderGrp(seed_field, query=True, value=True)
    time = cmds.intSliderGrp(time_field, query=True, value=True)
    export_animation = cmds.checkBoxGrp(animation_field, query=True, value1=True)
    start_frame =  cmds.intFieldGrp(frame_range_field, query=True, value1=True)
    end_frame =  cmds.intFieldGrp(frame_range_field, query=True, value2=True)
    export_normals = cmds.checkBoxGrp(normals_export_field, query=True, value1=True)
    invert_normals = cmds.checkBoxGrp(normals_invert_field, query=True, value1=True)
    check_Z = cmds.checkBoxGrp(check_Z_field, query = True, value1=True)
    
    materials = []
    for mat in range(5):
        materials.append(cmds.floatFieldGrp(eval('material_%i_field' % (mat)), query=True, value=True))
        
def get_file(field, mode):
    new_field_value = cmds.fileDialog2(caption='Select file', okCaption='OK', fileMode=mode)
    cmds.textFieldButtonGrp(field, edit = True, text = new_field_value[0])

def prepare():
    get_ui_values()
    selection = cmds.ls(sl=True)
    cmds.polyPlane(name = "REFPLANE", axis = [0,0,1], width = 1, height = float(resolution_y)/resolution_x, subdivisionsX=1, subdivisionsY=1)
    cmds.move(0.5,float(resolution_y)/resolution_x/2,0)
    ###CREATE ATTRIBUTES
    for obj in selection:
        shape = cmds.listRelatives(obj, shapes=True)
        if (cmds.objectType(shape, isType='pointLight') or cmds.objectType(shape, isType='spotLight')) and cmds.getAttr(obj + '.visibility'):
            cmds.select(shape[0])            
            if not 'hqzLightStart' in cmds.listAttr():
                cmds.addAttr(attributeType = 'float', niceName='HQZ Light Start', longName='hqzLightStart', shortName='hqzls', defaultValue=0, keyable=True)
            if not 'hqzLightEnd' in cmds.listAttr():
                cmds.addAttr(attributeType = 'float', niceName='HQZ Light End', longName='hqzLightEnd', shortName='hqzle', defaultValue=0, keyable=True)
            if not 'hqzSpectralLight' in cmds.listAttr():
                cmds.addAttr(attributeType = 'bool', niceName='HQZ Spectral Light', longName='hqzSpectralLight', shortName='hqzsl', defaultValue=True, keyable=True)
            if not 'hqzSpectralStart' in cmds.listAttr():
                cmds.addAttr(attributeType = 'float', niceName='HQZ Spectral Start', longName='hqzSpectralStart', shortName='hqzss', defaultValue=400, minValue=400, maxValue=700, keyable=True)
            if not 'hqzSpectralEnd' in cmds.listAttr():
                cmds.addAttr(attributeType = 'float', niceName='HQZ Spectral End', longName='hqzSpectralEnd', shortName='hqzse', defaultValue=700, minValue=400, maxValue=700, keyable=True)

        if cmds.objectType(shape, isType='spotLight') and cmds.getAttr(obj + '.visibility'):
            try:
                cmds.setAttr(obj+'.rotateX', 0)
                cmds.setAttr(obj+'.rotateY', -90)
                cmds.setAttr(obj+'.rotateY', lock = True)
                cmds.setAttr(obj+'.rotateX', lock = True)
            except:
                pass
            
        if cmds.objectType(shape, isType='mesh') and (cmds.getAttr(obj + '.v')):
            cmds.select(shape[0])
            if not 'hqzMaterial' in cmds.listAttr():
                cmds.addAttr(attributeType = 'byte', niceName='HQZ Material', longName='hqzMaterial', shortName='hqzmat', defaultValue = 0, keyable = True)
            cmds.polySoftEdge(obj, a = 180, ch = 0)
    
    try:
        cmds.select(selection, replace=True)
    except:
        pass
        
        
###START WRITING
def export():
    get_ui_values()
    selection = cmds.ls(sl=True)
    
    if export_animation:
        frame_range = range(start_frame, end_frame+1)
    else:
        frame_range = cmds.currentTime(query=True),
    
    for frame in frame_range:
        
        #####FRAME SETTINGS OVERRIDE GENERAL SETTINGS
        seed = frame
        
        
        
        cmds.currentTime(frame,edit=True)
        
        scene_code = ''
        
        scene_code += '{\n'#begin
        scene_code += '    "resolution": [' + str(resolution_x) + ', ' + str(resolution_y) + '],\n'
        scene_code += '    "viewport":  [0, 0, ' + str(resolution_x) + ', ' + str(resolution_y) + '],\n'
        scene_code += '    "exposure": ' + str(exposure) + ',\n'
        scene_code += '    "gamma": ' + str(gamma) + ',\n'
        scene_code += '    "rays": ' + str(ray_number) + ',\n'
        if time != 0:
            scene_code += '    "timelimit": ' + str(time) + ',\n'
        scene_code += '    "seed": ' + str(int(seed)) + ',\n'
        
        
        #### LIGHTS
        
        scene_code += '    "lights": [\n'
        
        for obj in cmds.ls(sl=True):
            shape = cmds.listRelatives(obj, shapes=True)
            if (cmds.objectType(shape, isType='pointLight') or cmds.objectType(shape, isType='spotLight')) and cmds.getAttr(obj + '.visibility'):
                wav = HSV2wavelength([cmds.getAttr(shape[0]+'.colorR'), cmds.getAttr(shape[0]+'.colorG'), cmds.getAttr(shape[0]+'.colorB')])
                use_spectral = cmds.getAttr(shape[0]+'.hqzSpectralLight')
                spectral_start = cmds.getAttr(shape[0]+'.hqzSpectralStart')
                spectral_end = cmds.getAttr(shape[0]+'.hqzSpectralEnd')
                x, y = get_loc(obj)
                y = resolution_y-y
                rot = get_rot(obj)
                
                scene_code += '        [ '
                scene_code += str(cmds.getAttr(shape[0]+'.intensity')) + ', '                 #LIGHT POWER
                scene_code += str(x) + ', '                                                   #XPOS
                scene_code += str(y)                                                          #YPOS
                scene_code += ', [0, 360], ['                                                 #POLAR ANGLE
                scene_code += str(cmds.getAttr(shape[0]+'.hqzLightStart')) + ', '             #POLAR DISTANCE MIN
                scene_code += str(cmds.getAttr(shape[0]+'.hqzLightEnd')) + '], ['             #POLAR DISTANCE MIN
                #scene_code += '0' + ', ' #POLAR DISTANCE MIN
                #scene_code += '0' + '], [' #POLAR DISTANCE MAX
                
                if cmds.objectType(shape, isType='spotLight'):
                    scene_code += str(-rot-cmds.getAttr(shape[0]+'.coneAngle')*0.5) + ', '               #ANGLE
                    scene_code += str(-rot+cmds.getAttr(shape[0]+'.coneAngle')*0.5) + '], '
                else:
                    scene_code += '0, 360], '
                    
                if use_spectral:
                    scene_code += '[' + str(spectral_start) + ', ' + str(spectral_end) + '] ],\n'                                         #WAVELENGTH
                else:
                    scene_code += str(int(wav))  +' ],\n'                                         #WAVELENGTH
                
        scene_code = scene_code[:-2]#remove last comma
        scene_code += '\n    ],\n'
        
        
        
        scene_code += '    "objects": [\n'
        
        
        #### GET MAYA EDGE LIST
        edge_list = []
        for obj in cmds.ls(sl=True):
            shape = cmds.listRelatives(obj, shapes=True)
            if cmds.objectType(shape, isType='mesh') and (cmds.getAttr(obj + '.v')):                
                cmds.select(obj)
                edge_info = cmds.polyInfo(edgeToVertex=True)#get vertices connected to edge
                #print(edge_info)
                edges=[]
                for edge in edge_info:
                    #print(edge)
                    edge = edge.split(':')[1]
                    edge = [int(edge[:7]),int(edge[8:15])]
                    #print(edge.split(' '))#get vertices connected to edge
                    edges.append(edge)
                #print(edges)
                
                
                for edge in edges:
                    edgev = []
        #                vertices = list(edge.vertices)
                    #material = obj.split('_')[1]
                    material = str(cmds.getAttr(shape[0]+'.hqzMaterial'))
                    edgev.append(material)
                    edgev.append(cmds.xform(obj+'.vtx[%i]' % edge[0], translation=True, absolute=True, worldSpace=True, query=True))
                    edgev.append(cmds.xform(obj+'.vtx[%i]' % edge[1], translation=True, absolute=True, worldSpace=True, query=True))
                    if export_normals:
                        #print(vertices[0])
                        for v_ix in range(2):
                            cmds.select(obj+'.vtx[%i]' % edge[v_ix])
                            vectors = cmds.polyNormalPerVertex(query=True, xyz=True)
                            edgev.append(cmds.xform(obj, rotation=True, query=True)[2] + vector2rotation([vectors[0],vectors[1]]))
                        #edgev.append((obj.rotation_euler[2] * 180/pi) + vector2rotation(obj.data.vertices[vertices[0]].normal))
                        #edgev.append((obj.rotation_euler[2] * 180/pi) + vector2rotation(obj.data.vertices[vertices[1]].normal))
                    
                    #print(edgev)
                    if check_Z:
                        #print(fabs(cmds.xform(obj+'.vtx[%i]' % edge[0], translation=True, absolute=True, worldSpace=True, query = True)[2]))
                        if fabs(cmds.xform(obj+'.vtx[%i]' % edge[0], translation=True, absolute=True, worldSpace=True, query=True)[2]) < 0.0001 \
                        and fabs( cmds.xform(obj+'.vtx[%i]' % edge[1], translation=True, absolute=True, worldSpace=True, query=True)[2]) < 0.0001:
                            edge_list.append(edgev)
                    else:
                        edge_list.append(edgev)
                    #print(edgev)
        #print(edge_list)
        
        ####OBJECTS
        for edge in edge_list:
            #print(edge)
            scene_code += '        [ '    
            scene_code += str(edge[0]) + ', '                                                    #MATERIAL
            scene_code += str(edge[1][0]*resolution_x) + ', '                                    #VERT1 XPOS
            scene_code += str(resolution_y - (edge[1][1]*resolution_x)) + ', '                   #VERT1 YPOS
            if export_normals:
                scene_code += str(edge[3]) + ', '                                                #VERT1 NORMAL
            
            scene_code += str(edge[2][0]*resolution_x - (edge[1][0]*resolution_x)) + ', '        #VERT2 DELTA XPOS 
            scene_code += str(-1 * (edge[2][1]*resolution_x - (edge[1][1]*resolution_x))) + '],' #VERT2 DELTA YPOS 
            if export_normals:
                scene_code = scene_code[:-2]#remove last comma and bracket
                normal = (edge[4]) - (edge[3])
                if normal < -180:
                    normal += 360
                if normal > 180:
                    normal -= 360
                scene_code += ',' + str(normal) + '],'                           #VERT2 NORMAL
            scene_code += '\n'
                
        scene_code = scene_code[:-2]#remove last comma
        
        scene_code += '\n    ],\n'
        scene_code += '    "materials": [\n'
        
        
        cmds.select(selection, replace=True)
        ###mats
        for mat in materials:
            scene_code += '        [ [ ' + str(mat[0]) + ', "d" ],[ ' + str(mat[1]) + ', "t" ],[ ' + str(mat[2]) + ', "r" ] ],\n'
                
        scene_code = scene_code[:-2]#remove last comma
        scene_code += '\n    ]\n'
        
        
        scene_code += '}'
        #print(scene_code)
        
        
        if debug:
            scene_code = scene_code.replace('\n', '')
            print(scene_code)
            
        
        #folder = folder
        save_path = folder + '\\' + file_name + '_' + str(int(frame)).zfill(4) + '.json'
        save_path.replace('/', '\\')
        print(save_path)
        d = os.path.dirname((folder+'\\').replace('/', '\\'))
        if not os.path.exists(d):
            os.makedirs(d)
            
        file = open(save_path, 'w')
        file.write(scene_code)
        file.close()
        
        
    ###DIRTY LOOP FOR BASH SCRIPT. I SHOULD LEARN THINGS
    if export_batch:
        shell_path = (folder+'\\').replace('/', '\\')+'batch.bat'
        shell_script = ''
        for frame in frame_range:
            shell_script +=  engine_path.replace('/', '\\') + ' ' + (folder+'\\').replace('/', '\\') + file_name + '_' + str(int(frame)).zfill(4) +".json "  + folder + '\\' + file_name + '_' + str(int(frame)).zfill(4) +".png\n"
        file = open(shell_path, 'w')
        file.write(shell_script)
        file.close()
    
    
####UI

spacing = 10
titleHeight = 20
cmds.window(title = "HQZ Exporter",width = 600)
cmds.columnLayout(adjustableColumn = True, bgc = [0.4,0.4,0.4])

cmds.frameLayout(label= 'File settings', borderStyle='in', collapsable=True, cl = 0)
engine_field = cmds.textFieldButtonGrp(label='Engine path', buttonLabel='Set...', buttonCommand = 'get_file(field = engine_field, mode = 1)')
directory_field = cmds.textFieldButtonGrp(label='Export directory', buttonLabel='Set...', buttonCommand = 'get_file(field = directory_field, mode = 3)')
file_field = cmds.textFieldGrp(label='File name')
batch_field = cmds.checkBoxGrp(label='Export batch file', value1 = True)
cmds.setParent("..")

cmds.frameLayout(label= 'Export settings', borderStyle='in', collapsable=True, cl = 0 )
cmds.text(label='Export settings', height = titleHeight)
resolution_field = cmds.intFieldGrp(numberOfFields=2, label='Scale', extraLabel='pixels', value1=1920, value2=1080)
exposure_field = cmds.floatSliderGrp(label='Exposure', field=True, value = 0.5, minValue = 0, maxValue = 10)
gamma_field = cmds.floatSliderGrp(label='Gamma', field=True, value = 2.2, minValue = 0, maxValue = 4)
rays_field = cmds.intSliderGrp(label='Number of rays', field=True, value = 100000, minValue = 0, maxValue = 100000, fieldMaxValue = 1000000000)
seed_field = cmds.intSliderGrp(label='Seed', field=True, value = 0, minValue = 0, maxValue = 100000, fieldMaxValue = 1000000000)
time_field = cmds.intSliderGrp(label='Render time', field=True, value = 20, minValue = 0, maxValue = 1000, fieldMaxValue = 100000000, extraLabel=' (0 for infinity)')
animation_field = cmds.checkBoxGrp(label='Export animation', value1=False, changeCommand='cmds.intFieldGrp(frame_range_field, edit=True, enable=cmds.checkBoxGrp(animation_field, query=True, value1=True))')
frame_range_field = cmds.intFieldGrp(numberOfFields=2, label='Frame range',  value1=1, value2=5, enable=False)
normals_export_field = cmds.checkBoxGrp(label='Export normals', value1 = True)
normals_invert_field = cmds.checkBoxGrp(label='Revert normals', value1 = False)
check_Z_field = cmds.checkBoxGrp(label='Check Z value', value1 = True)
cmds.setParent("..")

cmds.frameLayout(label='Material settings', borderStyle='in', collapsable=True, cl = 0)
cmds.text(label="Please enter factors for diffusion, transmission and reflection. The sum should be between 0 and 1.", height = titleHeight)
material_0_field = cmds.floatFieldGrp(numberOfFields=3, label='Material 0', value1=0.3, value2=0.3, value3=0.3)
material_1_field = cmds.floatFieldGrp(numberOfFields=3, label='Material 1', value1=0.0, value2=0.0, value3=0.0)
material_2_field = cmds.floatFieldGrp(numberOfFields=3, label='Material 2', value1=0.0, value2=0.0, value3=0.0)
material_3_field = cmds.floatFieldGrp(numberOfFields=3, label='Material 3', value1=0.0, value2=0.0, value3=0.0)
material_4_field = cmds.floatFieldGrp(numberOfFields=3, label='Material 4', value1=0.0, value2=0.0, value3=0.0)
cmds.setParent("..")

cmds.text(label="", height = spacing)
cmds.columnLayout(adjustableColumn = True, columnAttach=('both', 100))
cmds.rowLayout(numberOfColumns=2)
cmds.button(label='Prepare scene', command='prepare()')
cmds.button(label='Export', command='export()')
cmds.setParent("..")
cmds.setParent("..")
cmds.text(label="", height = spacing)
cmds.showWindow()

