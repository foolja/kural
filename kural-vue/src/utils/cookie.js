export const checkLoginStatus = () =>{
    const cookieString = document.cookie;
    const cookieArray = cookieString.split(';');
    const cookies = cookieArray.reduce((acc, cur) => {
      const [key, value] = cur.trim().split('=');
      acc[key] = decodeURIComponent(value);
      return acc;
    }, {});
  
    return cookies.sessionId ? true : false;
}