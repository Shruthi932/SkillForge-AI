/**
 * SkillForge Authentication Module
 * Provides session management, authentication checks, and password utilities
 */

(function() {
  'use strict';

  var AUTH_KEY = 'skillforgeAuth';
  var USERS_KEY = 'skillforgeUsers';
  var SESSION_KEY = 'skillforgeSession';

  // Simple hash function for passwords (not cryptographically secure, but better than plain text)
  // In production, use proper bcrypt or similar
  function hashPassword(password) {
    var hash = 0;
    for (var i = 0; i < password.length; i++) {
      var char = password.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
      hash = hash & hash;
    }
    // Add salt-like behavior with email
    return 'sf_' + Math.abs(hash).toString(16) + '_' + btoa(password).slice(0, 8);
  }

  function verifyPassword(password, hashedPassword) {
    return hashPassword(password) === hashedPassword;
  }

  // Get stored users
  function getUsers() {
    try {
      return JSON.parse(localStorage.getItem(USERS_KEY) || '[]');
    } catch (e) {
      return [];
    }
  }

  // Save users
  function saveUsers(users) {
    localStorage.setItem(USERS_KEY, JSON.stringify(users));
  }

  // Get current authenticated user
  function getCurrentUser() {
    var session = sessionStorage.getItem(SESSION_KEY);
    if (session) {
      try {
        return JSON.parse(session);
      } catch (e) {
        return null;
      }
    }
    // Check for "remember me" in localStorage
    var auth = localStorage.getItem(AUTH_KEY);
    if (auth) {
      try {
        var authData = JSON.parse(auth);
        sessionStorage.setItem(SESSION_KEY, JSON.stringify(authData));
        return authData;
      } catch (e) {
        return null;
      }
    }
    return null;
  }

  // Login function
  function login(email, password, role, rememberMe) {
    var normalizedEmail = email.trim().toLowerCase();
    var users = getUsers();
    
    var user = users.find(function(u) {
      return u.email === normalizedEmail && u.role === role;
    });

    if (!user) {
      return { success: false, message: 'Invalid credentials for ' + role + '.' };
    }

    // Verify password (either plain text or hashed)
    var isValid = user.password === password || verifyPassword(password, user.password);
    
    if (!isValid) {
      return { success: false, message: 'Invalid credentials for ' + role + '.' };
    }

    var sessionUser = {
      name: user.name,
      email: user.email,
      role: user.role,
      loginTime: Date.now()
    };

    // Set session
    sessionStorage.setItem(SESSION_KEY, JSON.stringify(sessionUser));

    // Remember me
    if (rememberMe) {
      localStorage.setItem(AUTH_KEY, JSON.stringify(sessionUser));
    } else {
      localStorage.removeItem(AUTH_KEY);
    }

    return { success: true, message: 'Login successful!', user: sessionUser };
  }

  // Logout function
  function logout() {
    sessionStorage.removeItem(SESSION_KEY);
    localStorage.removeItem(AUTH_KEY);
  }

  // Check if user is authenticated
  function isAuthenticated() {
    return getCurrentUser() !== null;
  }

  // Require authentication - redirects to login if not authenticated
  function requireAuth() {
    var user = getCurrentUser();
    if (!user) {
      window.location.href = 'login.html';
      return null;
    }
    return user;
  }

  // Register new user
  function register(name, email, password, role) {
    var normalizedEmail = email.trim().toLowerCase();
    var users = getUsers();

    // Check if user already exists
    var exists = users.some(function(u) {
      return u.email === normalizedEmail && u.role === role;
    });

    if (exists) {
      return { success: false, message: 'Account already exists for this role and email.' };
    }

    // Hash password before storing
    var hashedPassword = hashPassword(password);

    var newUser = {
      name: name,
      email: normalizedEmail,
      password: hashedPassword,
      role: role,
      createdAt: Date.now()
    };

    users.push(newUser);
    saveUsers(users);

    return { success: true, message: 'Account created successfully!' };
  }

  // Update password for current user
  function updatePassword(oldPassword, newPassword) {
    var user = getCurrentUser();
    if (!user) {
      return { success: false, message: 'Not logged in.' };
    }

    var users = getUsers();
    var userIndex = users.findIndex(function(u) {
      return u.email === user.email && u.role === user.role;
    });

    if (userIndex === -1) {
      return { success: false, message: 'User not found.' };
    }

    var storedUser = users[userIndex];
    
    // Verify old password
    var isValid = storedUser.password === oldPassword || verifyPassword(oldPassword, storedUser.password);
    
    if (!isValid) {
      return { success: false, message: 'Current password is incorrect.' };
    }

    // Update with new hashed password
    users[userIndex].password = hashPassword(newPassword);
    saveUsers(users);

    return { success: true, message: 'Password updated successfully!' };
  }

  // Expose functions globally
  window.SkillForgeAuth = {
    login: login,
    logout: logout,
    register: register,
    getCurrentUser: getCurrentUser,
    isAuthenticated: isAuthenticated,
    requireAuth: requireAuth,
    updatePassword: updatePassword,
    hashPassword: hashPassword
  };

})();

