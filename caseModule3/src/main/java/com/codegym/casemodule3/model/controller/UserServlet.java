package com.codegym.casemodule3.model.controller;
import com.codegym.casemodule3.model.User;
import com.codegym.casemodule3.model.service.UserDao;
import com.codegym.casemodule3.model.utils.ValidateUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "UserServlet", urlPatterns = "/users")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        userDao = new UserDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        try {
            switch (action) {
                case "create":
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteUser(request, response);
                    break;
                default:
                    listNumberPage(request, response);
                    break;
            }
        } catch (SQLException | ClassNotFoundException ex) {
            throw new ServletException(ex);
        }
    }

    private void listNumberPage(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ClassNotFoundException, ServletException, IOException {
        System.out.println("numberPage");
        int page = 1;
        int recordsPerPage = 5;
        if (req.getParameter("page") != null) {
            page = Integer.parseInt(req.getParameter("page"));
        };
        String name = "";
        if (req.getParameter("searchuser") != null) {
            name = req.getParameter("searchuser");
        }
        List<User> listUser = userDao.getNumberPage((page - 1) * recordsPerPage, recordsPerPage, name);
        int noOfRecords = userDao.getNoOfRecords();
        int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / recordsPerPage);
        req.setAttribute("listUser", listUser);
        req.setAttribute("noOfPages", noOfPages);
        req.setAttribute("currentPage", page);
        req.setAttribute("searchuser" , name);



        RequestDispatcher requestDispatcher = req.getRequestDispatcher("user/list.jsp");
        requestDispatcher.forward(req, resp);

    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/create.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String id = request.getParameter("id");
        User user = userDao.selectUser(id);
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/edit.jsp");
        request.setAttribute("user", user);
        dispatcher.forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        try {
            switch (action) {
                case "create":
                    insertUser(request, response);
                    break;
                case "edit":
                    updateUser(request, response);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    private void insertUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {

        User user;
        String fullName = request.getParameter("fullName").trim();
        String userName = request.getParameter("userName").replaceAll(" ", "").toLowerCase();
        String passWord = request.getParameter("passWord").trim();
        String email = request.getParameter("email").trim();
        String phone = request.getParameter("phone").trim();
        String address = request.getParameter("address").trim();
        List<String> errors = new ArrayList<>();
        boolean isPassword = ValidateUtil.isPasswordVailid(passWord);
        boolean isPhone = ValidateUtil.isNumberPhoneVailid(phone);
        boolean isEmail = ValidateUtil.isEmailValid(email);

        user = new User(fullName, userName, passWord, email, phone, address);
        if (userName.isEmpty() ||
                passWord.isEmpty() ||
                fullName.isEmpty() ||
                phone.isEmpty() ||
                email.isEmpty() ||
                address.isEmpty() ){
            errors.add("Vui lPlease fill out the information completely!");
        }
        if (userName.isEmpty()) {
            errors.add("UserName cannot be left blank!");
        }
        if (passWord.isEmpty()) {
            errors.add("Password cannot be left blank!");
        }
        if (fullName.isEmpty()) {
            errors.add("Fullname cannot be left blank!");
        }
        if (phone.isEmpty()) {
            errors.add("Phone incorrect input!");
        }
        if (!isPhone) {
            errors.add("Phone malformed");
        }
        if (userDao.existByPhone(phone)) {
            errors.add("Phone already exist!");
        }
        if (email.isEmpty()) {
            errors.add("Email incorrect input!");
        }
        if (!isEmail) {
            errors.add("Email malformed");
        }

        if (userDao.existsByEmail(email)) {
            errors.add("Email already exist!");
        }

        if (address.isEmpty()) {
            errors.add("Address cannot be left blank!");
        }

        if (userDao.existByUsername(userName)) {
            errors.add("Username already exist!");
        }
        if (!isPassword) {
            errors.add("Password malformed");
        }

        if (errors.size() == 0) {
            user = new User(fullName, userName, passWord, email, phone, address);
            boolean success = false;
            success = userDao.insertUser(user);

            if (success) {
                request.setAttribute("success", true);
            } else {
                request.setAttribute("errors", true);
                errors.add("Invalid data, Please check again!");
            }

        }
        if (errors.size() > 0) {
            request.setAttribute("errors", errors);
            request.setAttribute("user", user);
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/create.jsp");
        request.setAttribute("user", user);
        dispatcher.forward(request, response);
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        List<String> errors = new ArrayList<>();
        User user = null;
        String id= request.getParameter("id");

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        boolean isPhone = ValidateUtil.isNumberPhoneVailid(phone);
        boolean isEmail = ValidateUtil.isEmailValid(email);
        user = new User(id, fullName, phone,email, address);
        User userEmail = userDao.selectUser(id);
        String checkEmail = userEmail.getEmail();
        User userPhone = userDao.selectUser(id);
        String checkPhone = userPhone.getPhone();
        if (fullName.isEmpty() ||
                phone.isEmpty() ||
                email.isEmpty() ||
                address.isEmpty()) {
            errors.add("Please fill in all the information!");
        }

        if (fullName.isEmpty()) {
            errors.add("Full Name cannot be left blank!");
        }
        if (phone.isEmpty()) {
            errors.add("Phone cannot be left blank!");
        }
        if (email.isEmpty()) {
            errors.add("Email cannot be left blank!");
        }
        if (!isEmail) {
            errors.add("Email invalid format!");
        }

        if (userDao.existsByEmail(email) && !email.equals(checkEmail)){
            errors.add("Email already exist!");
        }
        if (!isPhone) {
            errors.add("Phone invalid format!");
        }
        if (userDao.existByPhone(phone) && !phone.equals(checkPhone)){
            errors.add("Phone already exist!");
        }
        if (address.isEmpty()) {
            errors.add("Address cannot be left blank!");
        }


        if (errors.size() == 0) {
            user = new User(id, fullName,email,phone, address);
            boolean success = false;
            success = userDao.updateUser(user);

            if (success) {
                request.setAttribute("success", true);
            } else {
                request.setAttribute("errors", true);
                errors.add("Invalid data, Please check again!");
            }
        }
        else {
            request.setAttribute("errors", errors);
            request.setAttribute("user", user);
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/edit.jsp");

        dispatcher.forward(request, response);
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        userDao.deleteUser(id);

        List<User> listUser = userDao.selectAllUsers();
        request.setAttribute("listUser", listUser);
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/list.jsp");
        dispatcher.forward(request, response);
    }

    private void search(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<User> listUser = null;
        String name = "";
        if (req.getParameter("searchuser") != null) {
            name = req.getParameter("searchuser");

            listUser = userDao.searchUser(name);
        } else {
            listUser = userDao.selectAllUsers();
        }
        req.setAttribute("listUser", listUser);
        RequestDispatcher dispatcher = req.getRequestDispatcher("user/list.jsp");
        dispatcher.forward(req, resp);
    }
}
