export function saveToken(token){
    localStorage.setItem("jwtToken",token)
}

export function getToken(){
    return localStorage.getItem("jwtToken")
}
export function deleteToken(){
    localStorage.removeItem("jwtToken")
}
