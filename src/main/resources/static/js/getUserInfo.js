export function getUserInfo(accessToken) {
    return fetch("/api/users/me", {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + accessToken
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("로그인 후 이용해주세요.");
            }
            console.log("사용자 정보를 성공적으로 불러왔습니다.");
            return response.json();
        })
        .then(user => {
            return {
                userId: user.data.userId,
                email: user.data.email,
                nickname: user.data.nickname,
                birth: user.data.birth,
                createdAt: user.data.createdAt
            };
        })
        .catch(error => {
            console.error(error);
            alert(error.message);
            window.location.href = "/login";
        });
}