const db = require('./db-connection');
const chalk = require('chalk');
const readline = require('readline');

// Cria a interface de linha de comando
const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
});

// Função para exibir o menu
function displayMenu() {
  console.clear();
  console.log(chalk.cyan('╔════════════════════════════════════════════╗'));
  console.log(chalk.cyan('║      Oracle FIAP - Utilitário de DB        ║'));
  console.log(chalk.cyan('╠════════════════════════════════════════════╣'));
  console.log(chalk.cyan('║ 1. Listar todas as tabelas                 ║'));
  console.log(chalk.cyan('║ 2. Verificar controle do Flyway            ║'));
  console.log(chalk.cyan('║ 3. Limpar esquema (remover todas tabelas)  ║'));
  console.log(chalk.cyan('║ 4. Executar SQL personalizado              ║'));
  console.log(chalk.cyan('║ 5. Verificar migrações do Flyway           ║'));
  console.log(chalk.cyan('║ 0. Sair                                    ║'));
  console.log(chalk.cyan('╚════════════════════════════════════════════╝'));
  console.log('');
}

// Função para listar todas as tabelas
async function listAllTables() {
  try {
    console.log(chalk.blue('\nListando todas as tabelas...\n'));
    
    const result = await db.executeQuery(`
      SELECT table_name
      FROM user_tables
      ORDER BY table_name
    `);
    
    if (result.rows.length === 0) {
      console.log(chalk.yellow('Nenhuma tabela encontrada no esquema.'));
    } else {
      console.log(chalk.green(`Encontradas ${result.rows.length} tabelas:`));
      result.rows.forEach((row, index) => {
        console.log(chalk.white(`${index+1}. ${row.TABLE_NAME}`));
      });
    }
  } catch (err) {
    console.error(chalk.red(`Erro: ${err.message}`));
  }
}

// Função para verificar o controle do Flyway
async function checkFlywayControl() {
  try {
    console.log(chalk.blue('\nVerificando controle do Flyway...\n'));
    
    // Verifica se a tabela de controle do Flyway existe
    const tableExistsResult = await db.executeQuery(`
      SELECT COUNT(*) as count
      FROM user_tables
      WHERE table_name = 'FLYWAY_SCHEMA_HISTORY'
    `);
    
    const flywayTableExists = tableExistsResult.rows[0].COUNT > 0;
    
    if (flywayTableExists) {
      console.log(chalk.green('✓ Tabela FLYWAY_SCHEMA_HISTORY encontrada!'));
      
      // Consulta as migrações executadas
      const migrationsResult = await db.executeQuery(`
        SELECT version, description, type, script, 
               TO_CHAR(installed_on, 'YYYY-MM-DD HH24:MI:SS') as installed_on,
               success
        FROM flyway_schema_history
        ORDER BY version
      `);
      
      if (migrationsResult.rows.length === 0) {
        console.log(chalk.yellow('Nenhuma migração registrada no histórico do Flyway.'));
      } else {
        console.log(chalk.green(`\nEncontradas ${migrationsResult.rows.length} migrações:`));
        
        migrationsResult.rows.forEach(row => {
          const status = row.SUCCESS === 1 ? chalk.green('✓ Sucesso') : chalk.red('✗ Falha');
          console.log(chalk.white(`\nVersão: ${row.VERSION}`));
          console.log(chalk.gray(`Descrição: ${row.DESCRIPTION}`));
          console.log(chalk.gray(`Script: ${row.SCRIPT}`));
          console.log(chalk.gray(`Instalado em: ${row.INSTALLED_ON}`));
          console.log(chalk.gray(`Status: ${status}`));
        });
      }
    } else {
      console.log(chalk.yellow('✗ Tabela FLYWAY_SCHEMA_HISTORY não encontrada!'));
      console.log(chalk.gray('O Flyway ainda não foi inicializado neste esquema.'));
    }
  } catch (err) {
    console.error(chalk.red(`Erro: ${err.message}`));
  }
}

// Função para limpar o esquema
async function cleanSchema() {
  try {
    console.log(chalk.blue('\nPreparando para limpar o esquema...\n'));
    
    const result = await db.executeQuery(`
      SELECT table_name
      FROM user_tables
      ORDER BY table_name
    `);
    
    const tables = result.rows;
    
    if (tables.length === 0) {
      console.log(chalk.yellow('Nenhuma tabela encontrada no esquema.'));
      return;
    }
    
    console.log(chalk.yellow(`AVISO: Esta ação irá remover ${tables.length} tabelas:\n`));
    tables.forEach(row => {
      console.log(chalk.white(`- ${row.TABLE_NAME}`));
    });
    
    rl.question(chalk.red('\n⚠️  Esta operação não pode ser desfeita! Deseja continuar? (sim/não): '), async (answer) => {
      if (answer.toLowerCase() === 'sim' || answer.toLowerCase() === 's') {
        console.log(chalk.blue('\nExecutando limpeza do esquema...'));
        
        // Primeiro, desabilita todas as constraints de chave estrangeira
        try {
          const constraintsResult = await db.executeQuery(`
            SELECT constraint_name, table_name
            FROM user_constraints
            WHERE constraint_type = 'R'
          `);
          
          for (const row of constraintsResult.rows) {
            await db.executeQuery(`
              ALTER TABLE ${row.TABLE_NAME} DISABLE CONSTRAINT ${row.CONSTRAINT_NAME}
            `);
            console.log(chalk.gray(`Desabilitada constraint ${row.CONSTRAINT_NAME}`));
          }
        } catch (err) {
          console.error(chalk.red(`Erro ao desabilitar constraints: ${err.message}`));
        }
        
        // Remove todas as tabelas
        for (const row of tables) {
          try {
            await db.executeQuery(`DROP TABLE ${row.TABLE_NAME} CASCADE CONSTRAINTS`);
            console.log(chalk.green(`Tabela ${row.TABLE_NAME} removida com sucesso!`));
          } catch (err) {
            console.error(chalk.red(`Erro ao remover tabela ${row.TABLE_NAME}: ${err.message}`));
          }
        }
        
        console.log(chalk.green('\nEsquema limpo com sucesso!'));
        promptForMenu();
      } else {
        console.log(chalk.blue('\nOperação cancelada pelo usuário.'));
        promptForMenu();
      }
    });
    return; // Evita chamar promptForMenu() duas vezes
  } catch (err) {
    console.error(chalk.red(`Erro: ${err.message}`));
  }
  
  promptForMenu();
}

// Função para executar SQL personalizado
function executeCustomSQL() {
  console.log(chalk.blue('\nExecutar SQL personalizado'));
  console.log(chalk.gray('Digite o comando SQL. Para concluir, digite uma linha vazia.'));
  
  let sql = '';
  const readSqlLine = () => {
    rl.question('> ', (line) => {
      if (line.trim() === '') {
        if (sql.trim() === '') {
          console.log(chalk.yellow('Nenhum SQL digitado. Operação cancelada.'));
          promptForMenu();
          return;
        }
        
        console.log(chalk.blue('\nExecutando consulta:'));
        console.log(chalk.gray(sql));
        console.log();
        
        db.executeQuery(sql)
          .then(result => {
            if (result.rows) {
              console.log(chalk.green(`Consulta executada com sucesso. ${result.rows.length} linhas retornadas.`));
              
              if (result.rows.length > 0) {
                const columns = Object.keys(result.rows[0]);
                console.log(chalk.cyan(`\n${'='.repeat(100)}`));
                console.log(columns.join(' | '));
                console.log(chalk.cyan(`${'='.repeat(100)}`));
                
                result.rows.forEach(row => {
                  console.log(columns.map(col => row[col] !== null ? row[col].toString() : 'NULL').join(' | '));
                });
                console.log(chalk.cyan(`${'='.repeat(100)}\n`));
              }
            } else {
              console.log(chalk.green(`Comando executado com sucesso. ${result.rowsAffected} linhas afetadas.`));
            }
          })
          .catch(err => {
            console.error(chalk.red(`Erro ao executar SQL: ${err.message}`));
          })
          .finally(() => {
            promptForMenu();
          });
      } else {
        sql += line + ' ';
        readSqlLine();
      }
    });
  };
  
  readSqlLine();
  return; // Evita chamar promptForMenu() duas vezes
}

// Função para verificar migrações do Flyway
async function checkFlywayMigrations() {
  try {
    console.log(chalk.blue('\nVerificando arquivos de migração e banco...\n'));
    
    // Verificar tabela flyway_schema_history
    const tableExistsResult = await db.executeQuery(`
      SELECT COUNT(*) as count
      FROM user_tables
      WHERE table_name = 'FLYWAY_SCHEMA_HISTORY'
    `);
    
    const flywayTableExists = tableExistsResult.rows[0].COUNT > 0;
    
    if (flywayTableExists) {
      console.log(chalk.green('✓ Tabela FLYWAY_SCHEMA_HISTORY encontrada!'));
      
      // Verificar tabelas de aplicação
      const tablesToCheck = [
        'NORMA_AMBIENTAL',
        'DEPARTAMENTO',
        'AUDITORIA',
        'CONFORMIDADE',
        'PENDENCIA',
        'LOG_CONFORMIDADE',
        'USUARIOS'
      ];
      
      console.log(chalk.blue('\nVerificando tabelas da aplicação:'));
      
      for (const tableName of tablesToCheck) {
        const exists = await db.executeQuery(`
          SELECT COUNT(*) as count
          FROM user_tables
          WHERE table_name = '${tableName}'
        `);
        
        if (exists.rows[0].COUNT > 0) {
          console.log(chalk.green(`✓ Tabela ${tableName} existe`));
        } else {
          console.log(chalk.red(`✗ Tabela ${tableName} não existe`));
        }
      }
    } else {
      console.log(chalk.yellow('✗ Tabela FLYWAY_SCHEMA_HISTORY não encontrada!'));
      console.log(chalk.gray('O Flyway ainda não foi inicializado neste esquema.'));
    }
  } catch (err) {
    console.error(chalk.red(`Erro: ${err.message}`));
  }
}

// Função para solicitar entrada do menu
function promptForMenu() {
  rl.question(chalk.blue('\nEscolha uma opção (0-5): '), async (option) => {
    switch (option) {
      case '1':
        await listAllTables();
        promptForMenu();
        break;
      case '2':
        await checkFlywayControl();
        promptForMenu();
        break;
      case '3':
        await cleanSchema();
        // Não chame promptForMenu() aqui, isso é feito dentro da função
        break;
      case '4':
        executeCustomSQL();
        // Não chame promptForMenu() aqui, isso é feito dentro da função
        break;
      case '5':
        await checkFlywayMigrations();
        promptForMenu();
        break;
      case '0':
        console.log(chalk.green('\nEncerrando aplicação...'));
        rl.close();
        await db.close();
        process.exit(0);
        break;
      default:
        console.log(chalk.red('\nOpção inválida. Por favor, escolha uma opção válida (0-5).'));
        promptForMenu();
        break;
    }
  });
}

// Função principal
async function main() {
  try {
    console.log(chalk.green('\nIniciando utilitário para Oracle FIAP...\n'));
    
    // Inicializa o pool de conexões
    const initialized = await db.initialize();
    
    if (!initialized) {
      console.error(chalk.red('\nFalha ao conectar ao banco de dados Oracle. Verifique as configurações e tente novamente.'));
      process.exit(1);
    }
    
    // Exibe o menu
    displayMenu();
    
    // Solicita entrada do usuário
    promptForMenu();
  } catch (err) {
    console.error(chalk.red(`Erro durante inicialização: ${err.message}`));
    process.exit(1);
  }
}

// Executa a função principal
main();

// Manipuladores para encerrar graciosamente
process.on('SIGINT', async () => {
  console.log(chalk.yellow('\nEncerrando aplicação...'));
  rl.close();
  await db.close();
  process.exit(0);
});
