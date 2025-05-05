FROM oraclelinux:8-slim

# Instalar cliente Oracle
RUN microdnf update -y && \
    microdnf install -y oracle-instantclient-release-el8 && \
    microdnf install -y oracle-instantclient-basic oracle-instantclient-sqlplus && \
    microdnf clean all

# Definir variáveis de ambiente
ENV PATH=$PATH:/usr/lib/oracle/21/client64/bin \
    LD_LIBRARY_PATH=/usr/lib/oracle/21/client64/lib \
    TNS_ADMIN=/usr/lib/oracle/21/client64/network/admin

# Diretório de trabalho
WORKDIR /scripts

# Entrada padrão
CMD ["bash"]
