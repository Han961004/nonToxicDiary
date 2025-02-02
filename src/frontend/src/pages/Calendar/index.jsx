import React from "react";
import styled from "@emotion/styled";
import ReportHeader from "../../components/features/Report/ReportHeader";
import Reports from "../../components/features/Report/Reports";
import MonthlyCalendar from "../../components/features/Calendar/MonthlyCalendar";
import { colors } from "../../styles/variants";
import { breakpoints } from "../../styles/variants";
import { UnderlinedButton } from "../../components/common/Button/UnderlinedButton";
import { useNavigate } from "react-router-dom";
import { path } from "../../routes/path";

export default function CalendarPage() {
    const nav = useNavigate();

    const moveToShare = () => {
        nav(path.share);
    };

    return (
        <Wrapper>
            <ReportSection>
                <ReportHeader></ReportHeader>
                <ShareButtonSection>
                    <UnderlinedButton onClick={moveToShare}>공유하기</UnderlinedButton>
                </ShareButtonSection>
                <Reports></Reports>
            </ReportSection>
            <CalendarSection>
                <CalendarTitle>내 캘린더</CalendarTitle>
                <MonthlyCalendar></MonthlyCalendar>
            </CalendarSection>
        </Wrapper>
    );
}

const Wrapper = styled.div`
    /* section_main */

    /* Auto layout */
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 0px 10px;
    gap: 10px;

    width: 100%;
`

const ReportSection = styled.div`
    /* report_section */

    /* Auto layout */
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 0px;
    isolation: isolate;

    width: 100%;
`

const ShareButtonSection = styled.div`
    margin-left: auto;
`

const CalendarSection = styled.div`
    /* section_calender */

    /* Auto layout */
    display: flex;
    flex-direction: column;
    align-items: stretch;
    
    padding: 0px;
    gap: 10px;

    width: 100%;
`

const CalendarTitle = styled.h1`
    /* 내 캘린더 */

    margin: 0 auto;

    font-family: 'Pretendard';
    font-style: normal;
    font-weight: 700;
    font-size: 28px;
    line-height: 33px;
    display: flex;
    align-items: center;
    text-align: center;
    letter-spacing: -0.05em;

    @media screen and (max-width: ${breakpoints.sm}) {
        font-size: 20px;
    }

    color: ${colors.mainGray}
`