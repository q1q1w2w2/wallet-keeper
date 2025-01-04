function logout() {
    const accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');
    fetch("/api/auth/logout", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + accessToken
        },
        body: JSON.stringify({
            accessToken: accessToken,
            refreshToken: refreshToken
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('로그아웃 실패');
            }
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('accessToken');
            window.location.href = '/login';
            alert("로그아웃 되었습니다.");
        })
        .catch(error => {
            console.error('Error:', error);
            alert('로그아웃 중 오류가 발생했습니다');
            window.location.href = '/login';
        });
}