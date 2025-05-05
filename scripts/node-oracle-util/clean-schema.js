const db = require('./db-connection');
const chalk = require('chalk');
const yargs = require('yargs/yargs');
const { hideBin } = require('yargs/helpers');

// Configuração dos argumentos de linha de comando
const argv = yargs(hideBin(process.argv))
  .option('force', {
    alias: 'f',
    type: 'boolean',
    description: 'Forçar limpeza sem confirmação',
    default: false
  })
  .option('tables', {
    alias: 't',
    type: 'array',
    description: 'Tabelas específicas para excluir',
    default: []
  })
  .option('list', {
    alias: 'l',
    type: 'boolean',
    description: 'Apenas listar as tabelas, sem excluir',
    default: false
  })
  .help()
  .alias('help', 'h')
  .argv;

// Função para obter todas as tabelas
async function getAllTables() {
  try {
    const result = await db.executeQuery(`
      SELECT table_name 
      FROM user_tables 
      ORDER BY table_name
    `);
    return result.rows.map(row => row.TABLE_NAME);
  } catch (err) {
    console.error(chalk.red(`Erro ao listar tabelas: ${err.message}`));
    return [];
  }
}

// Função para obter todas as restrições/constraints
async function getAllConstraints() {
  try {
    const result = await db.executeQuery(`
      SELECT constraint_name, table_name, constraint_type
      FROM user_constraints
      WHERE constraint_type IN ('R', 'P')
      ORDER BY constraint_type, table_name
    `);
    return result.rows;
  } catch (err) {
    console.error(chalk.red(`Erro ao listar constraints: ${err.message}`));
    return [];
  }
}

// Função para desativar todas as constraints de chave estrangeira
async function disableAllForeignKeys() {
  try {
    console.log(chalk.blue('Desativando constraints de chave estrangeira...'));
    
    const constraints = await getAllConstraints();
    const fkConstraints = constraints.filter(c => c.CONSTRAINT_TYPE === 'R');
    
    for (const constraint of fkConstraints) {
      const sql = `ALTER TABLE ${constraint.TABLE_NAME} DISABLE CONSTRAINT ${constraint.CONSTRAINT_NAME}`;
      await db.executeQuery(sql);
      console.log(chalk.gray(`  ↳ Desativada constraint ${constraint.CONSTRAINT_NAME} em ${constraint.TABLE_NAME}`));
    }
    
    console.log(chalk.green(`✓ ${fkConstraints.length} constraints desativadas com sucesso!`));
  } catch (err) {
    console.error(chalk.red(`Erro ao desativar constraints: ${err.message}`));
  }
}

// Função para excluir tabelas
async function dropTables(tables, force = false) {
  if (tables.length === 0) {
    console.log(chalk.yellow('Nenhuma tabela para excluir.'));
    return;
  }

  if (!force) {
    console.log(chalk.yellow(`⚠️  Esta operação excluirá ${tables.length} tabelas:`));
    tables.forEach(table => console.log(chalk.yellow(`   - ${table}`)));
    console.log(chalk.red('\n⚠️  AVISO: Esta operação não pode ser desfeita!'));
    console.log(chalk.yellow('Use a flag --force ou -f para confirmar.'));
    return;
  }

  // Desativa todas as foreign keys primeiro
  await disableAllForeignKeys();

  console.log(chalk.blue(`Excluindo ${tables.length} tabelas...`));
  
  // Exclui a tabela de controle do Flyway primeiro, se existir
  const flywayTable = tables.find(t => t === 'FLYWAY_SCHEMA_HISTORY');
  if (flywayTable) {
    try {
      await db.executeQuery(`DROP TABLE ${flywayTable}`);
      console.log(chalk.green(`✓ Tabela ${flywayTable} excluída com sucesso!`));
      tables = tables.filter(t => t !== flywayTable);
    } catch (err) {
      console.error(chalk.red(`Erro ao excluir tabela ${flywayTable}: ${err.message}`));
    }
  }

  // Exclui as demais tabelas
  for (const table of tables) {
    try {
      await db.executeQuery(`DROP TABLE ${table} CASCADE CONSTRAINTS`);
      console.log(chalk.green(`✓ Tabela ${table} excluída com sucesso!`));
    } catch (err) {
      console.error(chalk.red(`Erro ao excluir tabela ${table}: ${err.message}`));
    }
  }
}

// Função principal
async function main() {
  try {
    console.log(chalk.blue('\n🔄 Iniciando limpeza do esquema Oracle FIAP...\n'));
    
    // Inicializa o pool de conexões
    const initialized = await db.initialize();
    if (!initialized) {
      process.exit(1);
    }

    console.log(chalk.blue('\n📊 Verificando tabelas no esquema...\n'));
    const tables = await getAllTables();
    
    if (tables.length === 0) {
      console.log(chalk.green('✓ O esquema já está vazio. Nenhuma tabela encontrada.'));
    } else {
      console.log(chalk.blue(`Encontradas ${tables.length} tabelas no esquema:\n`));
      tables.forEach(table => console.log(chalk.gray(`  - ${table}`)));
      
      // Se for apenas para listar as tabelas
      if (argv.list) {
        console.log(chalk.yellow('\nExecutado com opção --list. Nenhuma tabela excluída.'));
      } else {
        // Se foram especificadas tabelas específicas
        const tablesToDrop = argv.tables.length > 0 
          ? tables.filter(t => argv.tables.includes(t))
          : tables;
        
        console.log(chalk.blue(`\n${tablesToDrop.length} tabelas selecionadas para exclusão.\n`));
        await dropTables(tablesToDrop, argv.force);
      }
    }
  } catch (err) {
    console.error(chalk.red(`\n❌ Erro durante execução: ${err.message}\n`));
  } finally {
    // Fecha o pool de conexões
    await db.close();
    console.log(chalk.blue('\n🏁 Operação concluída!\n'));
  }
}

// Executa a função principal
main();
