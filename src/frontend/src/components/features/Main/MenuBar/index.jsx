/* eslint-disable no-alert */
import styled from 'styled-components';
import { AiFillHome, AiFillEdit } from 'react-icons/ai';
import { FaCalendarAlt } from 'react-icons/fa';
import { FaFire } from 'react-icons/fa6';
import { IoLogOut } from 'react-icons/io5';
import { useNavigate } from 'react-router-dom';
import { colors } from '../../../../styles/variants';
import { IconContainer } from '../../../common/layouts/Icons';
import { path } from '../../../../routes/path';
import { handleLogout } from '../../../../api/instance';

export default function MenuBar() {
  const nav = useNavigate();
  const logoImg = `${import.meta.env.VITE_PUBLIC_URL}/svg/mango_logo.svg`;

  const moveToHome = () => {
    window.location.href = path.main;
  };

  const moveToTodayEat = () => {
    nav(path.todayEat);
  };

  const moveToCalendar = () => {
    nav(path.calendar); // 추후에 캘린더 페이지로 변경
  };

  const moveToChallenge = () => {
    nav(path.mychallengelist);
  };

  const handleConfirmLogout = () => {
    const isConfirm = window.confirm('정말 로그아웃 하시겠어요?');
    if (isConfirm) {
      handleLogout();
    }
  };

  return (
    <Wrapper>
      <button type="button" onClick={moveToHome}>
        <IconContainer src={logoImg} alt="고망다이어리 로고" width="45px" height="45px" />
      </button>
      <Container>
        <AiFillHome
          className="home"
          style={{ fontSize: '24px', cursor: 'pointer' }}
          onClick={moveToHome}
        />
        <AiFillEdit style={{ fontSize: '24px', cursor: 'pointer' }} onClick={moveToTodayEat} />
        <FaCalendarAlt style={{ fontSize: '24px', cursor: 'pointer' }} onClick={moveToCalendar} />
        <FaFire style={{ fontSize: '24px', cursor: 'pointer' }} onClick={moveToChallenge} />
        <IoLogOut style={{ fontSize: '28px', cursor: 'pointer' }} onClick={handleConfirmLogout} />
      </Container>
    </Wrapper>
  );
}

const Wrapper = styled.div`
  display: flex;
  align-items: center;
  height: 100%;
  max-height: 1024px;
  gap: 70px;
  border-radius: 20px;

  flex-direction: row;
  padding: 4px 8px;
  justify-content: space-between;
  width: 100%;
  background-color: transparent;
`;

const Container = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;

  flex-direction: row;
  margin: 0;
  gap: 12px;
  color: ${colors.mainOrange};
  padding: 0px 4px;
  .home {
    display: none;
  }
`;
