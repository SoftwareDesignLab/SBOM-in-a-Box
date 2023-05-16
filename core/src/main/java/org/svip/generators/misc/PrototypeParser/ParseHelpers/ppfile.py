import json

# Get filename
filename = input("Enter filename to convert (e.x. myFile.json): ")

# Attempt to open file
with open("./" + filename, "r+") as file:
   # Load in data
   data = json.load(file)

   # Overwrite raw json with PrettyPrinted json
   file.seek(0)
   file.write(json.dumps(data, indent=2))
   file.truncate()