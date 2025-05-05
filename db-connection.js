const oracledb = require('oracledb');
const readline = require('readline');

// Configurações de conexão
const config = {
  user: 'RM557024',
  password: '240200',
  connectString: 'oracle.fiap.com.br:1521/orcl'
};

// Interface para interação com o terminal
const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
});

// Função para executar uma consulta SQL
async function executeQuery(sql) {
  let connection;
  try {
    // Estabelecer conexão com o banco de dados
    connection = await oracledb.getConnection(config);
    console.log('Conexão estabelecida com sucesso!');

    // Executar a consulta
    const result = await connection.execute(sql, {}, { autoCommit: true });

    // Retornar o resultado
    return result;
  } catch (err) {
    console.error('Erro ao executar a consulta:', err);
  } finally {
    // Fechar a conexão
    if (connection) {
      try {
        await connection.close();
        console.log('Conexão fechada.');
      } catch (err) {
        console.error('Erro ao fechar a conexão:', err);
      }
    }
  }
}

// Função para listar todas as tabelas do usuário
async function listTables() {
  const sql = `SELECT table_name FROM user_tables ORDER BY table_name`;
  const result = await executeQuery(sql);
  
  if (result && result.rows.length > 0) {
    console.log('\nTabelas encontradas:');
    result.rows.forEach(row => {
      console.log(`- ${row[0]}`);
    });
  } else {
    console.log('Nenhuma tabela encontrada.');
  }
}

// Função para verificar se a tabela flyway_schema_history existe
async function checkFlywayTable() {
  const sql = `SELECT table_name FROM user_tables WHERE table_name = 'FLYWAY_SCHEMA_HISTORY'`;
  const result = await executeQuery(sql);
  
  if (result && result.rows.length > 0) {
    console.log('\nTabela Flyway encontrada. Histórico de migrações presente.');
    return true;
  } else {
    console.log('Tabela Flyway não encontrada. Nenhum histórico de migrações.');
    return false;
  }
}

// Função para dropar todas as tabelas do usuário (exceto tabelas do sistema)
async function dropAllTables() {
  const tables = await executeQuery(
    `SELECT table_name FROM user_tables ORDER BY table_name`
  );
  
  if (tables && tables.rows.length > 0) {
    console.log('\nDropando todas as tabelas...');
    
    for (const row of tables.rows) {
      const tableName = row[0];
      try {
        await executeQuery(`DROP TABLE ${tableName} CASCADE CONSTRAINTS`);
        console.log(`- Tabela ${tableName} removida com sucesso.`);
      } catch (err) {
        console.error(`- Erro ao remover tabela ${tableName}:`, err.message);
      }
    }
  } else {
    console.log('Nenhuma tabela para remover.');
  }
}

// Função para dropar todas as sequências do usuário
async function dropAllSequences() {
  const sequences = await executeQuery(
    `SELECT sequence_name FROM user_sequences ORDER BY sequence_name`
  );
  
  if (sequences && sequences.rows.length > 0) {
    console.log('\nDropando todas as sequências...');
    
    for (const row of sequences.rows) {
      const sequenceName = row[0];
      try {
        await executeQuery(`DROP SEQUENCE ${sequenceName}`);
        console.log(`- Sequência ${sequenceName} removida com sucesso.`);
      } catch (err) {
        console.error(`- Erro ao remover sequência ${sequenceName}:`, err.message);
      }
    }
  } else {
    console.log('Nenhuma sequência para remover.');
  }
}

// Função para limpar o esquema (remover todas as tabelas e sequências)
async function cleanSchema() {
  // Primeiro precisamos dropar todas as tabelas
  await dropAllTables();
  
  // Em seguida, dropar todas as sequências
  await dropAllSequences();
  
  console.log('\nEsquema limpo com sucesso!');
}

// Menu principal
async function showMenu() {
  console.log('\n=== GERENCIADOR DE BANCO DE DADOS ORACLE ===');
  console.log('1. Listar tabelas');
  console.log('2. Verificar tabela Flyway');
  console.log('3. Limpar esquema (CUIDADO: remove todas as tabelas e sequências)');
  console.log('4. Executar SQL personalizado');
  console.log('5. Sair');
  
  rl.question('\nEscolha uma opção: ', async (option) => {
    switch (option) {
      case '1':
        await listTables();
        showMenu();
        break;
      case '2':
        await checkFlywayTable();
        showMenu();
        break;
      case '3':
        rl.question('Tem certeza que deseja limpar todo o esquema? (sim/não): ', async (answer) => {
          if (answer.toLowerCase() === 'sim') {
            await cleanSchema();
          } else {
            console.log('Operação cancelada.');
          }
          showMenu();
        });
        break;
      case '4':
        rl.question('Digite o comando SQL a ser executado: ', async (sql) => {
          try {
            const result = await executeQuery(sql);
            if (result && result.rows) {
              console.log('\nResultado:');
              console.table(result.rows);
            }
          } catch (err) {
            console.error('Erro ao executar SQL:', err);
          }
          showMenu();
        });
        break;
      case '5':
        rl.close();
        console.log('Até logo!');
        process.exit(0);
        break;
      default:
        console.log('Opção inválida. Tente novamente.');
        showMenu();
    }
  });
}

// Iniciar programa
showMenu();
