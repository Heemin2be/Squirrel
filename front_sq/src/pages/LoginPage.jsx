import React from 'react';
import { Link } from 'react-router-dom';

function LoginPage() {
  return (
    <div>
      <h1>직원 로그인</h1>
      <form>
        <label>
          PIN:
          <input type="password" name="pin" />
        </label>
        <br />
        {/* This is a placeholder. In a real app, this would trigger an API call. */}
        <Link to="/pos">
          <button type="button">로그인</button>
        </Link>
      </form>
      <hr />
      <Link to="/kiosk">고객용 키오스크 바로가기</Link>
    </div>
  );
}

export default LoginPage;
