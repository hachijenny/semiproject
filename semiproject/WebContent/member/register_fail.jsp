<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
 	
	request.setCharacterEncoding("UTF-8");
	String cp = request.getContextPath();
  
 %>
<!DOCTYPE html>

<html>
<head>
<title>로그인 / 회원가입</title>
<link rel="stylesheet" href="<%=cp %>/css/loginstyle.css">
<script type="text/javascript">

		function goToLogin(){
			console.log("로그 이동")
	  	 	location.href="<%=cp %>/login/login.do";
		}
			
		</script>
</head>

<body background="<%=cp %>/img/member/background1.jpg">

	<div class="wrap">
		<div class="form-wrap" style="background-color: white;">
			<button class="submit" onclick="goToLogin();">
				<font size="3pt" color="white">ID EXISTS OR IS ENTERED INCORRECTLY.</font> <font
					size="3pt" color="white">PLEASE TRY AGAIN.</font>
			</button>
		</div>
	</div>

</body>