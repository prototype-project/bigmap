import time
import docker
import requests as req

'''
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)
docker network rm $(docker network ls)
'''

class Instance:
    def __init__(self, container, address):
        self.container = container
        self.address = address

def get_instance_network_address(client, network_name, instance):
    network = client.networks.get(network_name)
    for pk, c in network.attrs['Containers'].items():
        if c['Name'] == instance.container.name:
            return f"http://{c['IPv4Address'].split('/')[0]}:8080"
    else:
        raise RuntimeException('cannot find instance in network')


def healthy(port):
    try:
        resp = req.get(f'http://localhost:{port}/actuator/health')
        return resp.status_code == 200
    except req.exceptions.ConnectionError:
        return False

def create_network(client, network_name):
    return client.networks.create(network_name, driver='bridge')

def create_instances(client, number_of_instances, port_start, network_name):
    replicas = [Instance(
        client.containers.run(
            "beczkowb/io.bigmap",
            detach=True,
            network=network_name,
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

def configure_master(client, master, replicas, network_name):
    req.put(f'{master.address}/map/admin/set-as-master',
            json=[get_instance_network_address(client, network_name, r) for r in replicas])

def deploy():
    CONFIG = {
        'number_of_masters': 2,
        'number_of_replicas': 3,
        'number_of_routers': 10,
        'network_name': 'bigmap_cluster'
    }
    CLIENT = docker.from_env()
    START_PORT = 8080

    create_network(CLIENT, CONFIG['network_name'])
    all_replicas = []

    for master_i in range(CONFIG['number_of_masters']):
        port = START_PORT + master_i * CONFIG['number_of_replicas']
        replicas = create_instances(CLIENT, CONFIG['number_of_replicas'], port, CONFIG['network_name'])
        all_replicas.extend(replicas)
        configure_replicas(replicas, port)

    replicas_per_master = [all_replicas[i: i + CONFIG['number_of_replicas']] for i in range(0, len(all_replicas), CONFIG['number_of_replicas'])]
    for master_i, replicas_for_master in enumerate(replicas_per_master):
        port = START_PORT + CONFIG['number_of_masters'] * CONFIG['number_of_replicas'] + master_i
        master = create_instances(CLIENT, 1, port, CONFIG['network_name'])[0]
        configure_master(CLIENT, master, replicas_for_master, CONFIG['network_name'])

if __name__ == '__main__':
    deploy()