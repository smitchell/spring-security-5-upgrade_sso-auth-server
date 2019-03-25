<html>
<head>
  <link rel="stylesheet" type="text/css"
        href="webjars/bootstrap/css/bootstrap.min.css"/>
  <script type="text/javascript" src="webjars/jquery/jquery.min.js"></script>
  <script type="text/javascript"
          src="webjars/bootstrap/js/bootstrap.min.js"></script>
		  <style>
    .frame {
      height: 100%;
    }

    .vertical-center {
      min-height: 100%;
      /* Fallback for browsers do NOT support vh unit */
      min-height: 100vh;
      /* These two lines are counted as one :-)       */
      display: grid;
      align-items: center;
    }

    .frame.logo {
      background: linear-gradient(to right, #f2f6f7, #dfe8ea);
      text-align: center;
      vertical-align: middle;
    }

    .helper {
      display: inline-block;
      height: 100%;
      vertical-align: middle;
    }

    .form-control.underline {
      -webkit-box-shadow: 0 1px 0 #596267;
      box-shadow: 0 1px 0 #596267;
      -webkit-box-sizing: border-box;
      box-sizing: border-box;
      font-size: 18px;
      padding: 0 20px 20px 0;
      margin-top: 0;
      margin-right: 0;
      margin-bottom: 20px;
      margin-left: 20px;
      max-width: calc(100% - 40px);
      -moz-appearance: none;
      -ms-appearance: none;
      -webkit-appearance: none;
      appearance: none;
      border-radius: 0;
      display: inline-block;
      -webkit-box-flex: 1;
      -webkit-flex: 1;
      -ms-flex: 1;
      flex: 1;
      border: 0;
      background: transparent;
      height: auto;
    }
    .form-control.underline:focus{
      -webkit-box-shadow: 0 2px 0 #00aeef;
      box-shadow: 0 2px 0 #00aeef;
    }

    h2 {
      color: #596267;
      margin-left: 20px
    }

    label {
      color: #596267;
      font-size: 12px;
      font-weight: bold;
      margin: 20px 0 5px 20px;
    }

    .btn-primary {
      background-color: #00aeef;
      border-color: #00aeef;
    }
    .btn-log{
      margin-right: 20px;
    }
  </style>
</head>
<body>
<#if RequestParameters['error']??>
	<div class="alert alert-danger">
    There was a problem logging in. Please try again.
  </div>
</#if>
  <form role="form" action="login" method="post">
    <h2>Login</h2>
      <div class="form-group">
        <label for="username">Username</label>
        <input type="text" class="form-control underline" id="username" name="username" />
      </div>
      <div class="form-group">
        <input type="password" placeholder="Password" class="form-control underline" id="password" name="password" />
      </div>
      <div class="form-group">
        <button type="submit" class="btn btn-primary btn-lg pull-right btn-log">Log In</button>
        <button type="button" class="btn btn-link btn-lg pull-right btn-log">Forgot Password?</button>
      </div>
      <input type="hidden" id="csrf_token" name="${_csrf.parameterName}" value="${_csrf.token}" />
  </form>
</body>
</html>