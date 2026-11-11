import os
import re
import json

def scan_controllers():
    controller_dir = 'backend/src/main/java/com/aquasafe/controller'
    results = {}
    for fname in sorted(os.listdir(controller_dir)):
        if fname.endswith('.java'):
            path = os.path.join(controller_dir, fname)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
            req_match = re.search(r'@RequestMapping\(["\']([^"\']+)["\']\)', content)
            base = req_match.group(1) if req_match else ''
            endpoints = re.findall(r'@(GetMapping|PostMapping|PutMapping|DeleteMapping)(?:\(["\']([^"\']*)["\']\))?', content)
            results[fname] = {'base': base, 'endpoints': [(m, f"{base}{ep}") for m, ep in endpoints]}
    return results

def scan_entities():
    entity_dir = 'backend/src/main/java/com/aquasafe/entity'
    entities = {}
    for fname in sorted(os.listdir(entity_dir)):
        if fname.endswith('.java'):
            path = os.path.join(entity_dir, fname)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
            table_match = re.search(r'@Table\(name\s*=\s*["\']([^"\']+)["\']', content)
            table_name = table_match.group(1) if table_match else fname[:-5].lower()
            fields = re.findall(r'private\s+([\w<>]+)\s+(\w+);', content)
            entities[fname[:-5]] = {'table': table_name, 'fields': fields}
    return entities

def scan_datasets():
    datasets = {}
    for root, dirs, files in os.walk('data'):
        for f in files:
            if f.endswith('.csv'):
                p = os.path.join(root, f)
                with open(p, 'r', encoding='utf-8', errors='ignore') as fp:
                    lines = fp.readlines()
                count = len(lines) - 1 if len(lines) > 0 else 0
                header = lines[0].strip() if lines else ''
                datasets[p.replace('\\', '/')] = {'count': count, 'header': header}
    return datasets

def scan_frontend():
    frontend_dir = 'frontend'
    files = {}
    for f in sorted(os.listdir(frontend_dir)):
        if f.endswith('.html'):
            p = os.path.join(frontend_dir, f)
            with open(p, 'r', encoding='utf-8') as fp:
                txt = fp.read()
            # find api calls or scripts
            scripts = re.findall(r'src=["\']([^"\']+)["\']', txt)
            links = re.findall(r'href=["\']([^"\']+\.html)["\']', txt)
            files[f] = {'scripts': scripts, 'links': sorted(list(set(links)))}
    return files

if __name__ == '__main__':
    controllers = scan_controllers()
    print("=== CONTROLLERS & ENDPOINTS ===")
    total_ep = 0
    for c, info in controllers.items():
        print(f"\n{c} ({info['base']}):")
        for m, ep in info['endpoints']:
            total_ep += 1
            print(f"  [{m.upper()}] {ep}")
    print(f"\nTotal Endpoints: {total_ep}")

    print("\n=== DATASETS ===")
    datasets = scan_datasets()
    for p, info in datasets.items():
        print(f"{p}: {info['count']} records | Columns: {len(info['header'].split(','))}")

    print("\n=== ENTITIES ===")
    entities = scan_entities()
    for e, info in entities.items():
        print(f"{e} -> Table: {info['table']} ({len(info['fields'])} fields)")

