import React, { useState, useEffect } from 'react';
import './Home.css';
import { useNavigate } from 'react-router-dom';

function getSecondElement(subjectClassCode) {
    const parts = subjectClassCode.split('-');
    return parts[1];
}

function Home(){
    const navigate = useNavigate()
    const [listStudent, setListStudent] = useState([])
    const [selectedStudent, setSelectedStudent] = useState(null)
    const [listSubject, setListSubject] = useState([])
    const [selectedSubject, setSelectedSubject] = useState(null)
    const [listAllClass, setListAllClass] = useState([])
    const [listSubjectClass, setListSubjectClass] = useState([])
    const [details, setDetails] = useState([]);
    const [selectedClass, setSelectedClass] = useState(null)
    const [registration, setRegistration] = useState(null)

    useEffect(() => {
        fetch('http://localhost:8080/student/all/')
            .then(response => response.json())
            .then(data =>{
                console.log(data)
                setListStudent(data)
            })
            .catch(err => console.error('Error fetching students: ', err))

        fetch('http://localhost:8081/subject/all/')
            .then(response => response.json())
            .then(data => {
                console.log(data)
                setListSubject(data)
            })
            .catch(err => console.error('Error fetching subjects: ', err))
        
        fetch(`http://localhost:8082/subject-class/all/`)
            .then(response => response.json())
            .then(data => {
                console.log(data)
                setListAllClass(data)
            })
            .catch(err => console.error('Error fetching registration: ', err));
        
    }, [])
    
    useEffect(() => {
        if(selectedStudent){
            fetch(`http://localhost:8083/registration/${selectedStudent.code}`)
                .then(response => response.json())
                .then(data => {
                    console.log(data)
                    setRegistration(data)
                })
                .catch(err => console.error('Error fetching subject classes: ', err));
        }
    }, [selectedStudent])

    useEffect(() => {
        if (selectedSubject) {
            const data = listAllClass.filter((classItem) => classItem.subjectCode == selectedSubject.code);
            setListSubjectClass(data);
            console.log(data)
        }
    }, [selectedSubject]);
    
    useEffect(() => {
        if (registration) {
            registration.details.forEach(detail => {
                setDetails(prevDetails => [
                    ...prevDetails,
                    {
                        subjectCode: detail.subjectClassCode.split('-')[0],
                        subjectClassCode: detail.subjectClassCode
                    }
                ]);
            });
        }
    }, [registration]);
    

    const handleStudentChange = (event) => {
        const selectedStudentCode = event.target.value;
        const selectedStudent = listStudent.find(student => student.code === selectedStudentCode);
        setSelectedStudent(selectedStudent);
    };
    

    const handleSubjectChange = (event) => {
        const selectedSubjectCode = event.target.value;
        const selectedSubject = listSubject.find(subject => subject.code === selectedSubjectCode);
        setSelectedSubject(selectedSubject);

        const selectSubjectDetails = details.find(detail => detail.subjectCode === selectedSubjectCode)
        if(selectSubjectDetails){
            setSelectedClass(selectSubjectDetails.subjectClassCode)
        }
    };

    const handleClassSelection = (classCode) => {
        const subjectCode = classCode.split('-')[0];
        const isSubjectSelected = details.some((detail) => detail.subjectCode === subjectCode);
    
        if (isSubjectSelected) {
            const updatedDetails = details.filter((detail) => detail.subjectCode !== subjectCode);
            setDetails(updatedDetails);
        }
        setDetails((prevDetails) => [
            ...prevDetails,
            {
                subjectCode: subjectCode,
                subjectClassCode: classCode
            }
        ]);
        setSelectedClass(classCode);
    };
    const handDeleteDetail = (subjectClassCode) =>{
        const updatedDetails = details.filter((detail) => detail.subjectClassCode !== subjectClassCode);
        setDetails(updatedDetails)
        if(subjectClassCode === selectedClass){
            setSelectedClass(null)
        }
    }

    const handSave = () =>{
        const studentRegister = {
            id: registration ? registration.id : 0,
            studentCode: selectedStudent.code,
            details: details
        }
        navigate(`/process-bar/`, { state: { studentRegister: studentRegister } })
    }
    
    return (
        <div>
            <h1>Đăng ký học</h1>
            <div className='select-registration'>
                <div className='dropdown'>
                    <select id="student" onChange={handleStudentChange}>
                        <option value="" disabled selected hidden>Chọn sinh viên</option>
                        {listStudent.map(student => (
                            <option key={student.code} value={student.code}>{student.lastname + student.firstname}</option>
                        ))}
                    </select>
                </div>
                <div className='dropdown'>
                    <select id="subject" onChange={handleSubjectChange} >
                        <option value="" disabled selected hidden>Chọn môn học</option>
                        {listSubject.map(subject => (
                            <option key={subject.code} value={subject.code}>{subject.name}</option>
                        ))}
                    </select>
                </div>
                <div className='show-table'>
                    <table>
                        <thead>
                            <tr>
                                <th>Mã MH</th>
                                <th>Tên môn học</th>
                                <th>Nhóm</th>
                                <th>Số tín</th>
                                <th>Sĩ số Max</th>
                                <th>Sĩ số còn</th>
                                <th>Thứ</th>
                                <th>Tiết bắt đầu</th>
                                <th>Tiết kết thúc</th>
                                <th>Thời gian học</th>
                                <th>Chọn</th>
                            </tr>
                        </thead>
                        <tbody>
                            {listSubjectClass.map(subjectClass =>(
                                <tr key={subjectClass.code}>
                                    <td>{selectedSubject.code}</td>
                                    <td>{selectedSubject.name}</td>
                                    <td>{getSecondElement(subjectClass.code)}</td>
                                    <td>{selectedSubject.credits}</td>
                                    <td>{subjectClass.maxNumber}</td>
                                    <td>{subjectClass.number}</td>
                                    <td>
                                        <ul>
                                            {subjectClass.schedules.map(schedule => (
                                                <li key={schedule.id}>
                                                    {schedule.dayOfWeek}
                                                </li>
                                            ))}
                                        </ul>
                                    </td>
                                    <td>
                                        <ul>
                                            {subjectClass.schedules.map(schedule => (
                                                <li key={schedule.id}>
                                                    {schedule.sessionStart}
                                                </li>
                                            ))}
                                        </ul>
                                    </td>
                                    <td>
                                        <ul>
                                            {subjectClass.schedules.map(schedule => (
                                                <li key={schedule.id}>
                                                    {schedule.sessionEnd}
                                                </li>
                                            ))}
                                        </ul>
                                    </td>
                                    <td>
                                         <ul>
                                            {subjectClass.schedules.map(schedule => (
                                                <li key={schedule.id}>
                                                    {schedule.weeks.join(', ')}
                                                </li>
                                            ))}
                                        </ul>
                                    </td>
                                    <td>
                                        <input 
                                            type='checkbox'
                                            checked={subjectClass.code === selectedClass}
                                            onChange={() => handleClassSelection(subjectClass.code)}/>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
            <div className='show-registration'>
                <h3>Đơn đăng ký</h3>
                <div className='show-table'>
                    <table>
                        <thead>
                            <tr>
                                <th>Mã MH</th>
                                <th>Tên môn học</th>
                                <th>Nhóm</th>
                                <th>Số tín</th>
                                <th>Thứ</th>
                                <th>Tiết bắt đầu</th>
                                <th>Tiết kết thúc</th>
                                <th>Thời gian học</th>
                                <th>Xóa</th>
                            </tr>
                        </thead>
                        <tbody>
                            {details.map(detail => {
                                const subject = listSubject.find(s => s.code === detail.subjectCode)
                                const subjectClass = listAllClass.find(c => c.code === detail.subjectClassCode)
                                return(
                                    <tr key={subject.code}>
                                        <td>{subject.code}</td>
                                        <td>{subject.name}</td>
                                        <td>{getSecondElement(subjectClass.code)}</td>
                                        <td>{subject.credits}</td>
                                        <td>
                                            <ul>
                                                {subjectClass.schedules.map(schedule => (
                                                    <li key={schedule.id}>
                                                        {schedule.dayOfWeek}
                                                    </li>
                                                ))}
                                            </ul>
                                        </td>
                                        <td>
                                            <ul>
                                                {subjectClass.schedules.map(schedule => (
                                                    <li key={schedule.id}>
                                                        {schedule.sessionStart}
                                                    </li>
                                                ))}
                                            </ul>
                                        </td>
                                        <td>
                                            <ul>
                                                {subjectClass.schedules.map(schedule => (
                                                    <li key={schedule.id}>
                                                        {schedule.sessionEnd}
                                                    </li>
                                                ))}
                                            </ul>
                                        </td>
                                        <td>
                                            <ul>
                                                {subjectClass.schedules.map(schedule => (
                                                    <li key={schedule.id}>
                                                        {schedule.weeks.join(', ')}
                                                    </li>
                                                ))}
                                            </ul>
                                        </td>
                                        <td>
                                            <button onClick={() => handDeleteDetail(subjectClass.code)}>Xóa</button>
                                        </td>
                                    </tr>
                                )
                            })}
                        </tbody>
                    </table>
                </div>
            </div>
            <div className='action-save'>
                {details.length > 0 && selectedStudent ? <button onClick={() => handSave()}>Lưu</button> : ''}
            </div>
        </div>
    )
}
export default Home;