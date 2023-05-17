#!/bin/python3
#Written By: Schuyler Dillon, Dylan Mulligan

import json


#set of components from SBOM generator, and parser
parseSet = set()
sbomSet = set()

#getting the name of the components and putting it into SBOM list
with open("./BenchmarkData/Control Projects/Python/Util/ParseHelpers/sbom.json") as file:
   data = json.load(file)
   for component in data["components"]:
        #component example: {'type': 'library', 'bom-ref': '41f394b3-ca54-479e-877c-3f53707a3e09', 'name': 'PyPDF2', 'version': '3.0.1', 'purl': 'pkg:pypi/pypdf2@3.0.1'}
        #grabs just the name, but can get version later if needed
        sbomSet.add(component["name"])




#getting the name of components and putting it into parseList
with open("./BenchmarkData/Control Projects/Python/Util/ParseHelpers/nppy-master.dep.json") as file:
   data = file.read()
   dict = json.loads(data)
   for component in dict["dependencies"]: 
        #omitting internal dependencies cause their dependencies are already accounted for
        if "INTERNAL" not in component['type']:
            #from the parser, if the component has a "from" then grab that
            #i.e. import bar from foo will say foo is the main dependency
            if "from" in component.keys():
                parseSet.add(component["from"])
            
            #if there isn't a from then you just import the name of the component
            #i.e. import os -> name=os
            else:
                parseSet.add(component["name"])



sbomListUniq = list(sbomSet - parseSet)
parseListUniq = list(parseSet - sbomSet)
#sorting both for visual comparisons
sbomListUniq.sort()
parseListUniq.sort()

#print out the unique lists of things found when generating the sbom but not from the parser
print("found in SBOM but not parser")
print(sbomListUniq)
print()
print()
#print out  the unique list of things found when parsing but not while generating the sbom
print("found in parse but not SBOM")
print(parseListUniq)


# #prints both for visual comparisons
# print(sbomList)
# print()
# print(parseList)