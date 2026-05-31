import requests

url = 'localhost:8080'

response = requests.get(url)

# GETs
if response.status_code == 200:
    dados = response.json()
    print(dados)
else:
    print("Erro na requisição:", response.status_code)

# POSTs
dados = {}

respons = requests.post(url, json=dados)
print(response.json())

# PUTs
dados = {}

response = requests.put(url, json=dados)
print(response.json())

# DELETE
response = requests.delete(url + '/del')

print(response.json())
