const fs = require('fs');
const path = require('path');

// Ler o arquivo base da collection
const baseCollection = JSON.parse(fs.readFileSync(path.join(__dirname, 'verdicomply-api-collection.json'), 'utf8'));

// Ler e combinar os arquivos de requests
const authRequests = JSON.parse(fs.readFileSync(path.join(__dirname, 'auth_requests.json'), 'utf8'));
const healthRequests = JSON.parse(fs.readFileSync(path.join(__dirname, 'health_requests.json'), 'utf8'));
const auditoriasRequests = JSON.parse(fs.readFileSync(path.join(__dirname, 'auditorias_requests.json'), 'utf8'));
const conformidadesRequests = JSON.parse(fs.readFileSync(path.join(__dirname, 'conformidades_requests.json'), 'utf8'));
const logsRequests = JSON.parse(fs.readFileSync(path.join(__dirname, 'logs_requests.json'), 'utf8'));
const pendenciasRequests = JSON.parse(fs.readFileSync(path.join(__dirname, 'pendencias_requests.json'), 'utf8'));

// Adicionar os requests à collection nas pastas correspondentes
baseCollection.item[0].item = authRequests;
baseCollection.item[1].item = healthRequests;
baseCollection.item[2].item = auditoriasRequests;
baseCollection.item[3].item = conformidadesRequests;
baseCollection.item[4].item = logsRequests;
baseCollection.item[5].item = pendenciasRequests;

// Escrever a collection completa
fs.writeFileSync(path.join(__dirname, 'verdicomply-api-collection-complete.json'), JSON.stringify(baseCollection, null, 2));

console.log('Collection completa criada com sucesso!');
