echo current directory: %~dp0..

set PROJECT_DIR=%~dp0../..

echo remove directory: %PROJECT_DIR%/agileway-shiro-redis-springdata2/src
rmdir /S/Q "%PROJECT_DIR%/agileway-shiro-redis-springdata2/target"
rmdir /S/Q "%PROJECT_DIR%/agileway-shiro-redis-springdata2/src"

echo 0