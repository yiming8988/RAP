package com.baidu.rigel.rap.project.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.baidu.rigel.rap.account.bo.User;
import com.baidu.rigel.rap.account.dao.AccountDao;
import com.baidu.rigel.rap.project.bo.Module;
import com.baidu.rigel.rap.project.bo.Page;
import com.baidu.rigel.rap.project.bo.Project;
import com.baidu.rigel.rap.project.dao.ProjectDao;
import com.baidu.rigel.rap.project.service.ProjectMgr;

public class ProjectMgrImpl implements ProjectMgr {

	private ProjectDao projectDao;
	
	public ProjectDao getProjectDao() {
		return this.projectDao;
	}
	
	public void setProjectDao(ProjectDao projectDao) {
		this.projectDao = projectDao;
	}
	
	private AccountDao accountDao;
	
	public AccountDao getAccountDao () {
		return accountDao;
	}
	
	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}
	
	@Override
	public List<Project> getProjectList(User user, int curPageNum, int pageSize) {
		if (user.isUserInRole("admin")) {
			user = null;
		}	
		List<Project> projectList = projectDao.getProjectList(user, curPageNum, pageSize);
		for (Project p : projectList) {
			if (user == null || p.getUser().getId() == user.getId())
				p.setIsManagable(true);
		}
		return projectList;	
	}

	@Override
	public int addProject(Project project) {
		for (String account : project.getMemberAccountList()) {
			User user = accountDao.getUser(account);
			if (user != null) {
				project.addMember(user);
			}
		}
		return projectDao.addProject(project);
	}

	@Override
	public int removeProject(int id) {
		return projectDao.removeProject(id);
	}

	@Override
	public int updateProject(Project outerProject) {
		Project project = getProject(outerProject.getId());
		project.setName(outerProject.getName());
		project.setIntroduction(outerProject.getIntroduction());
		
		if (outerProject.getMemberAccountList() != null) {
			// adding new ones
			for (String account : outerProject.getMemberAccountList()) {
				User user = accountDao.getUser(account);
				if (user != null) {
					project.addMember(user);
				}
			}
		
		
			if (project.getUserList() != null) {
				// remove old ones
				List<User> userListToBeRemoved = new ArrayList<User>();
				for (User user : project.getUserList()) {
					if (!outerProject.getMemberAccountList().contains(user.getAccount())) {
						userListToBeRemoved.add(user);
					}
				}		
				
				for (User user : userListToBeRemoved) {
					project.removeMember(user);
				}
			}
		}
		
		return projectDao.updateProject(project);
	}
	
	@Override
	public Project getProject(int id) {
		return projectDao.getProject(id);
	}

	@Override
	public Module getModule(int id) {
		return projectDao.getModule(id);
	}
	
	@Override
	public Page getPage(int id) {
		return projectDao.getPage(id);
	}
	
	@Override
	public String updateProject(int id, String projectData, String deletedObjectListData) {		
		return projectDao.updateProject(id, projectData, deletedObjectListData);
	}

	@Override
	public long getProjectListNum(User user) {
		if (user.isUserInRole("admin")) {
			user = null;
		}	
		return projectDao.getProjectListNum(user);
	}
}
