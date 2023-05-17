#Written By: Schuyler Dillon
#reads in a requirements.txt file
import subprocess
import re

requirementsPath = "./Control Projects/Python/Util/ParseHelpers/requirements.txt"

with open(requirementsPath) as file:
    #resets file every time program is called
    open("NewRequirements.txt", "w").close()
    #opens NewRequirements file 
    f = open("NewRequirements.txt", "a")
    modules = file.readlines()
    
    #each line should have seperate modules
    for module in modules:
        #searches each line for [module]==[version]. == and version are optional
        modVersion = re.search("^(?:(?!;)([\w-]*)(?:==([\d.]*))?)", module)


        #check first if a second entry exists, if not, query pip for latest version
        if modVersion.group(2) is None:
            mod = modVersion.group(1)
            #runs subprocess to get the output to the pip show command for each module
            result = subprocess.run(["pip", "show", mod], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            #converts it into english
            output = result.stdout.decode("utf-8").strip().split("\n")
            for line in output:
                #for each line in the decoded string, find the one where Version:[version#]
                if line.startswith("Version:"):
                    #write to NewRequirements.txt file
                    f.write(mod+"=="+line.split(':')[1].strip()+"\n")
        else:
            #just write the original [module]==[version] to the new file
            f.write(modVersion.group(1)+"=="+modVersion.group(2)+"\n")

        
       


    f.close()