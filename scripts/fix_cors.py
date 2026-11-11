import glob

controllers = glob.glob('backend/src/main/java/com/aquasafe/controller/*.java')
count = 0
for path in controllers:
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    if '@CrossOrigin(origins = "*")' in content:
        content = content.replace('@CrossOrigin(origins = "*")', '@CrossOrigin(originPatterns = "*")')
        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)
        count += 1
        print(f"Updated CORS in: {path}")

print(f"Total controllers updated: {count}")

