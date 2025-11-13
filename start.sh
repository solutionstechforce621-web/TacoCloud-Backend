#!/bin/bash

GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${GREEN}🚀 Iniciando build del proyecto Spring Boot...${NC}"
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
  echo -e "${RED}❌ Falló el build de Maven.${NC}"
  exit 1
fi

echo -e "${GREEN}🐳 Construyendo la imagen Docker de TacoCloud-API...${NC}"
docker compose build tacocloud-api

if [ $? -ne 0 ]; then
  echo -e "${RED}❌ Falló la construcción de la imagen Docker.${NC}"
  exit 1
fi

echo -e "${GREEN}📦 Levantando servicios con Docker Compose...${NC}"
docker compose up -d

if [ $? -ne 0 ]; then
  echo -e "${RED}❌ Error al levantar los contenedores.${NC}"
  exit 1
fi

echo -e "${GREEN}✅ Servicios levantados correctamente.${NC}"
echo -e "${GREEN}🔍 Verifica logs con:${NC} docker logs -f tacocloud-api"
echo -e "${GREEN}🌐 Accede a la API en:${NC} http://localhost:8085"
