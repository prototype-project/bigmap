import time
import docker
import requests as req

'''
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)
'''

class Instance:
    def __init__(self, container, address):
        self.container = container
        self.address = address

def healthy(port):
    try:
        resp = req.get(f'http://localhost:{port}/actuator/health')
        return resp.status_code == 200
    except req.exceptions.ConnectionError:
        return False

def create_network(client, network_name):
    return client.networks.create(network_name, driver='bridge')

def create_instances(client, number_of_instances, port_start):
    replicas = [Instance(
        client.containers.run(
            "beczkowb/io.bigmap",
            detach=True,
            ports={'8080/tcp': port_start + i}),
        f'http://localhost:{port_start + i}'
    ) for i in range(number_of_instances)]

    while not all([healthy(port_start + i) for i in range(number_of_instances)]):
        time.sleep(1)

    return replicas

def configure_replicas(replicas, port_start):
    for i, r in enumerate(replicas):
        req.put(
            f'http://localhost:{port_start + i}/map/admin/set-as-replica')

def connect_instances_to_network(network, instances):
    for i in instances:
        network.connect(i.container)

def configure_master(master, replicas):
    req.put(f'{master.address}/map/admin/set-as-master', json=[r.address for r in replicas])

def deploy():
    CONFIG = {
        'number_of_masters': 2,
        'number_of_replicas': 3,
        'number_of_routers': 10,
        'network_name': 'bigmap_cluster'
    }
    CLIENT = docker.from_env()
    START_PORT = 8080

    network = create_network(CLIENT, CONFIG['network_name'])
    all_replicas = []

    for master_i in range(CONFIG['number_of_masters']):
        port = START_PORT + master_i * CONFIG['number_of_replicas']
        replicas = create_instances(CLIENT, CONFIG['number_of_replicas'], port)
        all_replicas.extend(replicas)
        configure_replicas(replicas, port)
        connect_instances_to_network(network, replicas)

    replicas_per_master = [all_replicas[i: i + CONFIG['number_of_replicas']] for i in range(0, len(all_replicas), CONFIG['number_of_replicas'])]
    for master_i, replicas_for_master in enumerate(replicas_per_master):
        port = START_PORT + CONFIG['number_of_masters'] * CONFIG['number_of_replicas'] + master_i
        master = create_instances(CLIENT, 1, port)[0]
        connect_instances_to_network(network, [master])
        configure_master(master, replicas_for_master)

if __name__ == '__main__':
    deploy()