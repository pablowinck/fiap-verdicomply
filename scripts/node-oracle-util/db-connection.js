const oracledb = require('oracledb');
const dotenv = require('dotenv');
const chalk = require('chalk');

// Carrega variáveis de ambiente
dotenv.config();

// Configuração do Oracle
const dbConfig = {
  user: process.env.ORACLE_USER,
  password: process.env.ORACLE_PASSWORD,
  connectString: process.env.ORACLE_CONNECT_STRING,
};

// Inicializa o pool de conexões
async function initialize() {
  try {
    console.log(chalk.blue('Inicializando pool de conexões Oracle...'));
    
    // Configure o oracledb conforme necessário
    oracledb.outFormat = oracledb.OUT_FORMAT_OBJECT;
    oracledb.autoCommit = true;
    
    // Cria o pool de conexões
    await oracledb.createPool({
      ...dbConfig,
      poolAlias: 'oraclePool',
      poolIncrement: 1,
      poolMin: 1,
      poolMax: 5,
      poolTimeout: 60
    });
    
    console.log(chalk.green('✓ Pool de conexões Oracle inicializado com sucesso!'));
    return true;
  } catch (err) {
    console.error(chalk.red(`Erro ao inicializar pool de conexões Oracle: ${err.message}`));
    return false;
  }
}

// Executa consulta SQL
async function executeQuery(sql, binds = [], options = {}) {
  let connection;
  
  try {
    // Obtem uma conexão do pool
    connection = await oracledb.getConnection('oraclePool');
    
    // Executa a consulta
    const result = await connection.execute(sql, binds, options);
    return result;
  } catch (err) {
    console.error(chalk.red(`Erro ao executar consulta: ${err.message}`));
    console.error(chalk.yellow(`SQL: ${sql}`));
    
    // Fornecer orientações específicas para erros comuns em ambiente acadêmico
    if (err.message.includes('insufficient privileges')) {
      console.log(chalk.yellow('\nDica: Você não tem privilégios suficientes para esta operação.'));
      console.log(chalk.yellow('Em ambientes acadêmicos, geralmente você só pode operar em tabelas do seu próprio esquema.'));
    } else if (err.message.includes('ORA-00942')) {
      console.log(chalk.yellow('\nDica: A tabela mencionada não existe ou você não tem acesso a ela.'));
    }
    
    throw err;
  } finally {
    // Libera a conexão de volta para o pool
    if (connection) {
      try {
        await connection.close();
      } catch (err) {
        console.error(chalk.red(`Erro ao fechar conexão: ${err.message}`));
      }
    }
  }
}

// Fecha o pool de conexões
async function close() {
  try {
    console.log(chalk.blue('Fechando pool de conexões Oracle...'));
    await oracledb.getPool('oraclePool').close(0);
    console.log(chalk.green('✓ Pool de conexões Oracle fechado com sucesso!'));
  } catch (err) {
    console.error(chalk.red(`Erro ao fechar pool de conexões Oracle: ${err.message}`));
  }
}

module.exports = {
  initialize,
  executeQuery,
  close
};
