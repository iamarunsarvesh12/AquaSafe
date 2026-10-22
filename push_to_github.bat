@echo off
setlocal
cd /d "%~dp0"
title AquaSafe - Push to GitHub

echo ==============================================================================
echo                     AQUASAFE - PUSH TO GITHUB REPOSITORY                      
echo                      https://github.com/iamarunsarvesh12/AquaSafe
echo ==============================================================================
echo.

:: Ensure MinGit is in path if not already in system path
where git >nul 2>&1
if %errorlevel% neq 0 (
    set "PATH=C:\Users\Admin\.mingit\cmd;%PATH%"
)

echo [*] Remote URL:
git remote -v
echo.
echo [*] Pushing main branch to GitHub...
echo (If prompted, sign in via browser or paste your GitHub Personal Access Token)
echo.

git push -u origin main

echo.
if %errorlevel% equ 0 (
    echo [+] Successfully pushed all project files to:
    echo     https://github.com/iamarunsarvesh12/AquaSafe
) else (
    echo [!] Push failed or cancelled.
    echo If GitHub asks for a password, note that GitHub requires a Personal Access Token (PAT).
    echo Generate one at: https://github.com/settings/tokens (with 'repo' scope)
)
echo.
pause
