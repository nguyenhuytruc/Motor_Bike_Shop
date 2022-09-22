package com.codegym.casemodule3.model.controller;

import com.codegym.casemodule3.model.service.IProductDao;
import com.codegym.casemodule3.model.service.IUserDao;
import com.codegym.casemodule3.model.service.ProductDao;
import com.codegym.casemodule3.model.service.UserDao;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    private IUserDao iUserDao;
    IProductDao iProductDao;

    @Override
    public void init() throws ServletException {
        iUserDao = new UserDao();
        iProductDao = new ProductDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("product/login.jsp");
        requestDispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password= req.getParameter("password");

        if(iUserDao.checkUserExists(username, password)){
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/products");
            requestDispatcher.forward(req,resp);
        }else{
            req.setAttribute("err","This account is Invalid");
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("product/login.jsp");
            requestDispatcher.forward(req, resp);
        }
    }

}




