import axios from 'axios';
import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Card, Space, Table, Typography, Button, Steps, notification } from 'antd';
import { LoadingOutlined } from '@ant-design/icons';

const { Step } = Steps;

const STATUS = {
    process: "process",
    error: "error",
    finish: "finish",
    wait: "wait",
};

function ShowProcessBar(){
    const studentRegister = useLocation().state.studentRegister;
    const navigate = useNavigate()
    const [current, setCurrent] = useState(0);
    const [statusCancel, setStatusCancel] = useState(false);
    const [status, setStatus] = useState(STATUS.process);

    useEffect(() => {
        setCurrent(0);
        setStatus(STATUS.process);
        if(studentRegister !== null){
            axios.post("http://localhost:8084/task/", studentRegister)
                .then(response => {
                    setStatus(STATUS.finish);
                    setStatusCancel(true)
                    console.log(response.status)
                })
                .catch((err) => {
                    setStatusCancel(true)
                    console.error("Error: " + err)
                    setStatus(STATUS.error);
                })
        }
    }, [])

    useEffect(() => {
        if(status === STATUS.process && studentRegister !== null){
            console.log(studentRegister)
            const eventSource = new EventSource("http://localhost:8084/task/"+studentRegister.studentCode)
        
            eventSource.onopen = () => {
                console.log('SSE connection opened');
            };

            eventSource.onmessage = (event) => {
                const data = JSON.parse(event.data);
                console.log('Received SSE data:', data);
                const lastTrueId = data.filter(item => item.status === true).pop()?.id;
                setCurrent(lastTrueId+1);
                if (lastTrueId === 9) setStatusCancel(true);
            };

            eventSource.addEventListener('completion', () => {
                eventSource.close();
                console.log('SSE connection closed');
            });

            eventSource.onerror = (error) => {
                if (error.status !== 200) {
                    console.error('SSE error:', error);
                    eventSource.close();
                    console.log('SSE connection closed due to error');
                }
            };
        }
    }, [])

    const handleCancel = () => {
        setStatusCancel(true);
        setStatus(STATUS.wait);
        axios.delete(`http://localhost:8084/task/`+studentRegister.studentCode)
            .then((response) => {})
            .catch((error) => {
                console.error("Error:", error);
            });
    };

    const onBack = () =>{
        navigate(`/`)
    }


    return (
        <Space size={20} direction="vertical">
            <Typography.Title level={9}>Register Processing</Typography.Title>
            <Card style={{ minWidth: '660px', minHeight: '380px'}}>
                <Space direction="vertical">
                    {studentRegister && (
                        <>
                            <Space direction="horizontal" style={{paddingTop: '10px', paddingBottom: '10px'}}>
                                <div className="student" style={{marginBottom:'50px'}}>Processing</div>
                            </Space>
                            <div style={{ minWidth: '1600px'}}>
                                <Steps current={current} labelPlacement="vertical" status={status}>
                                    <Step title="Start" icon={current === 0 && status === STATUS.process ? <LoadingOutlined /> : undefined}></Step>
                                    <Step title="Get Student" icon={current === 1 && status === STATUS.process ? <LoadingOutlined /> : undefined}></Step>
                                    <Step title="Get Subject" icon={current === 2 && status === STATUS.process ? <LoadingOutlined /> : undefined}></Step>
                                    <Step title="Verify credits" icon={current === 3 && status === STATUS.process ? <LoadingOutlined /> : undefined}></Step>
                                    <Step title="Get Progress" icon={current === 4 && status === STATUS.process ? <LoadingOutlined /> : undefined}></Step>
                                    <Step title="Verify prerequisites" icon={current === 5 && status === STATUS.process ? <LoadingOutlined /> : undefined}></Step>
                                    <Step title="Get Schedule of Subject Class" icon={current === 6 && status === STATUS.process ? <LoadingOutlined /> : undefined}></Step>
                                    <Step title="Verify schedule" icon={current === 7 && status === STATUS.process ? <LoadingOutlined /> : undefined}></Step>
                                    <Step title="Verify number" icon={current === 8 && status === STATUS.process ? <LoadingOutlined /> : undefined}></Step>
                                    <Step title="Register"  icon={current === 9 && status === STATUS.process ? <LoadingOutlined /> : undefined}></Step>
                                </Steps>
                                <div style={{marginTop:'50px', textAlign:'right', marginRight:'30px'}}>
                                    {!statusCancel ? (
                                        <Button type="primary" onClick={handleCancel}>Hủy</Button>
                                    ) : (
                                        <Button type="default" onClick={onBack}>Trở về</Button>
                                    )}
                                </div>
                            </div>
                        </>
                    )}
                </Space>
            </Card>
        </Space>
    )
}

export default ShowProcessBar;
