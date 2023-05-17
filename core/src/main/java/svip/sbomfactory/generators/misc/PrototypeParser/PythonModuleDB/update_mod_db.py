"""
Utility Program to get all the listed Python Modules into local database

@author Derek Garcia
"""
import datetime
import json
import re
import sys

import requests

PY_MODULE_DB = 'PY_MODULE_DB.json'

# URL Requests
PY_ROOT_URL = 'https://docs.python.org/3/'
PY_MODULES_URL = PY_ROOT_URL + 'py-modindex.html'

# For Regex Expressions
PY_MOD_TABLE_START = '<table class=\"indextable modindextable\">'
PY_MOD_TABLE_END = '</table>'

PY_MOD_START = '<code class="xref">'
PY_MOD_END = '</code>'

PY_REF_START = 'href="'
PY_REF_END = '">'

"""
Requests the Module page from Python and gets module information

@param output path, defaults to pwd
"""
if __name__ == '__main__':
    # Get entire page
    print("Requesting Information. . .")
    response = requests.get(PY_MODULES_URL)
    assert (response.status_code == 200), "Request Failed"

    # Get Table of modules
    print("Parsing Data. . .")
    mod_table = re.search(
        PY_MOD_TABLE_START + r"(.*)" + PY_MOD_TABLE_END,
        response.text.replace('\n', '')
    )
    assert (mod_table is not None), "Parsing Table Failed"

    # Get Each row information
    mod_table_data = re.findall(
        "<a(.*?)</a>",
        mod_table.group(1)
    )
    assert (mod_table_data is not None), "Parsing Table Components Failed"

    # Build the module database row by row
    module_db = {
        "modules": {}
    }
    for mod_data in mod_table_data:
        try:
            # Attempt to get module name
            try:
                mod_name = re.search(
                    PY_MOD_START + r'(.*)' + PY_MOD_END,
                    mod_data
                ).group(1)
            except:
                print("Parsing Name Failed")

            # Attempt to get bom ref
            try:
                mod_ref = re.search(
                    PY_REF_START + r'(.*?)' + PY_REF_END,
                    mod_data
                ).group(1)
            except:
                print("Parsing Ref Failed")

            # Add to DB
            module_db["modules"][mod_name] = PY_ROOT_URL + mod_ref
        except:
            print ("Parsing failed for '" + mod_data + "'; skipping . . .\n")

    # Don't write to file if no information
    if len(module_db["modules"]) == 0:
        print("module database is empty, skipping writing to file")
        print("Complete!")
        exit(0)

    # add time generated tag
    module_db["generated"] = datetime.datetime.now().isoformat()

    # Write to destination if given
    path = PY_MODULE_DB
    if len(sys.argv) != 1:
        path = sys.argv[1] + '\\' + path

    # Attempt to write to path, if fails default to pwd
    try:
        with open(path, "w") as out:
            out.write(json.dumps(module_db, indent=4))
    except:
        print("Couldn't write to '" + path + "'; defaulting to current directory")
        with open(PY_MODULE_DB, "w") as out:
            out.write(json.dumps(module_db, indent=4))

    print("Complete!")
    exit(0)
