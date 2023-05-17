"""
Basic package scraper to help analyze larger sets of data
!!! THIS SHOULD NOT BE USED ON ITS OWN !!!
It should complement manual parsing of files

@author Derek Garcia
"""

import os
import sys
import json
import re

DEFAULT_DB_PATH = "Control Projects/Python/Util/PY_MODULE_DB.json"

# DOES NOT ACCOUNT FOR 'from a import b'
IMPORT_RE_V1 = r'^import (.*?)(?: as|$|\n)'
IMPORT_RE_V2 = r'^from (.*?)(?:$| )'

"""
Load Module DB from path

@:param path location of DB
@:return DB if found, none otherwise
"""


def get_db(path=DEFAULT_DB_PATH):
    # Attempt Given path
    try:
        return json.load(open(path))
    except:
        print("Failed to open '" + path + "'; Attempting Default path")

    # Attempt Default path
    try:
        return json.load(open(DEFAULT_DB_PATH))
    except:
        print("Failed to open '" + DEFAULT_DB_PATH + "'; Generate a new DB and retry")

    # return none if both fail
    return None


def isInternal(package, master_root):
    path = package.split(".")

    if len(path) != 1:
        path = path[1:]
    path = "\\".join(path)
    base = master_root + "\\" + path
    # cwd = pwd + "\\" + path
    py = base + ".py"
    pyi = base + ".pyi"
    return os.path.exists(base) or os.path.exists(py) or os.path.exists(pyi)



"""
Parses the given root directory and check if the packages found are in-built or external

@:param module_db DB to check packages against
@:param root root path to start at
"""


def parse(module_db, root):
    master_root = root
    print('Root: ' + os.path.abspath(root))
    out = ""  # recursive parse from root
    for root, dir, files in os.walk(root):
        if root == "C:\Users\dlg1206\SBOM Project\lib-test\env\Lib\site-packages\\numpy\\array_api":
            print "foo"
        print('Now Parsing Directory: ' + root)
        out += 'Now Parsing Directory: ' + root + '\n'

        # Test each file
        for filename in files:
            # Attempt to open and parse each file
            try:
                file_path = os.path.join(root, filename)
                with open(file_path, "r") as f:
                    print("\tParsing: " + filename)
                    out += "\tParsing: " + filename + "\n"

                    # Load and apply regex
                    content = f.read()
                    packages = []
                    reg1 = re.findall(IMPORT_RE_V1, content, flags=re.MULTILINE)
                    packages.extend(reg1)

                    reg2 = re.findall(IMPORT_RE_V2, content, flags=re.MULTILINE)
                    packages.extend(reg2)

                    # Report Findings
                    if len(packages) == 0:
                        print ("\t\tNo Packages Found")
                        out += "\t\tNo Packages Found\n"
                    else:
                        # List all packages found
                        for package in packages:
                            package = package.split(" ")[0]
                            msg = "\t\tPackage: " + package

                            if package in module_db["modules"]:
                                msg += " | Python Package"

                            if package in root.split("\\") or isInternal(package, root):
                                msg += " | Internal Package"

                            # Check if in file path DB
                            if package not in module_db["modules"] and package not in root.split("\\") and not isInternal(package, master_root):
                                msg = msg + " | EXTERNAL PACKAGE"
                            if package + ".py" in root.split("\\"):
                                msg = msg + " | EXTERNAL PACKAGE"

                            out += msg + "\n"
                            print(msg)

            except Exception as e:
                out += "Error parsing '" + filename + "'; skipping . . .\n"
                print("Error parsing '" + filename + "'; skipping . . .")
                print(e.message)

    with open("out.txt", "w") as f:
        f.write(out)


"""
Main driver. Loads DB and begins parsing

@:param Target directory
@:param Module DB
"""
if __name__ == '__main__':
    if len(sys.argv) == 1:
        print ("Scraper needs Source directory")
        exit(0)

    target_dir = sys.argv[1]

    module_db = None
    if len(sys.argv) == 3:
        module_db = get_db(sys.argv[2])
    else:
        module_db = get_db()

    if module_db is None:
        exit(0)

    parse(module_db, target_dir)

    print ("Complete!")
