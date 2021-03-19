echo current directory: %~dp0..

set PROJECT_DIR=%~dp0..

echo remove directory: %PROJECT_DIR%/agileway-redis-springdata2/src
rmdir /S/Q "%PROJECT_DIR%/agileway-redis-springdata2/src"

mkdir "%PROJECT_DIR%/agileway-redis-springdata2/src"
echo xcopy "%PROJECT_DIR%/agileway-redis\src\*" -> "%PROJECT_DIR%/agileway-redis-springdata2/src/"

xcopy /E/c/f "%PROJECT_DIR%/agileway-redis\src\*" "%PROJECT_DIR%/agileway-redis-springdata2/src/"
cd ..